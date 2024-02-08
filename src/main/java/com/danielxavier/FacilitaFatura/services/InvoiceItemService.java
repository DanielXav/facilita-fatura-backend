package com.danielxavier.FacilitaFatura.services;


import com.danielxavier.FacilitaFatura.entities.InvoiceItem;
import com.danielxavier.FacilitaFatura.repositories.InvoiceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InvoiceItemService {

    @Autowired
    private InvoiceItemRepository repository;

    private static final Pattern PATTERN_1 = Pattern.compile(
            "\\@?(\\d{2}/\\d{2})\\s*(.*?)\\s*(\\d{2}/\\d{2})?\\s*\\n?((\\d{1,3}(?:[.,]\\d{3})*[.,]\\d{2})|\\d+[.,]\\d{2})"
    );

    private static final Pattern PATTERN_2 = Pattern.compile(
            "\\@?(\\d{2}/\\d{2})\\s*\\n(.*?)\\s*(\\d{2}/\\d{2})?\\s*((\\d{1,3}(?:[.,]\\d{3})*[.,]\\d{2})|\\d+[.,]\\d{2})"

            );

    private static final Pattern PATTERN_3 = Pattern.compile(
            "(\\d{2}/\\d{2})\\s*(.*?)\\s*(\\d{2}/\\d{2})?\\s*\\n((\\d{1,3}(?:[.,]\\d{3})*[.,]\\d{2})|\\d+[.,]\\d{2})"

            );

    private static final Pattern PATTERN_4 = Pattern.compile(
            "(\\d{2}/\\d{2})\\s*\\n(.*?)\\s*(\\d{2}/\\d{2})?\\s*\\n((\\d{1,3}(?:[.,]\\d{3})*[.,]\\d{2})|\\d+[.,]\\d{2})"

            );

    private static final Pattern PATTERN_5 = Pattern.compile(
            "(\\d{2}/\\d{2})\\s*\\n(.*?)\\s*\\n(\\d{2}/\\d{2})?\\s*((\\d{1,3}(?:[.,]\\d{3})*[.,]\\d{2})|\\d+[.,]\\d{2})"

            );

    private static final Pattern PATTERN_6 = Pattern.compile(
            "(\\d{2}/\\d{2})\\s*\\n(.*?)\\s*(\\d{2}/\\d{2})?\\s*\\n((\\d{1,3}(?:[.,]\\d{3})*[.,]\\d{2})|\\d+[.,]\\d{2})"

            );

    private static final Pattern PATTERN_7 = Pattern.compile(
            "(\\d{2}\\s*(?:jan|fev|mar|abr|mai|jun|jul|ago|set|out|nov|dez))\\s*(.*?)\\s*-\\s*Parcela\\s*(\\d+/\\d+)\\s*\\n(\\d{1,3}(?:[.,]\\d{3})*[.,]\\d{2})"
    );

    private static final Map<String, String> MONTH_MAP = new HashMap<>();
    static {
        MONTH_MAP.put("jan", "01");
        MONTH_MAP.put("fev", "02");
        MONTH_MAP.put("mar", "03");
        MONTH_MAP.put("abr", "04");
        MONTH_MAP.put("mai", "05");
        MONTH_MAP.put("jun", "06");
        MONTH_MAP.put("jul", "07");
        MONTH_MAP.put("ago", "08");
        MONTH_MAP.put("set", "09");
        MONTH_MAP.put("out", "10");
        MONTH_MAP.put("nov", "11");
        MONTH_MAP.put("dez", "12");
    }

    public String determineBrand(String textract) {
        if (textract.contains("Pagamentos efetuados") || textract.contains("Lançamentos: compras e saques")) {
            return "Hipercard";
        } else {
            return "C6";
        }
    }

    public List<InvoiceItem> parseInvoiceItems(String text, String brand) {
        List<InvoiceItem> allItems = new ArrayList<>();
        allItems.addAll(parsePattern(text, PATTERN_1, brand));
        allItems.addAll(parsePattern(text, PATTERN_2, brand));
        allItems.addAll(parsePattern(text, PATTERN_3, brand));
        allItems.addAll(parsePattern(text, PATTERN_6, brand));
        allItems.addAll(parsePattern(text, PATTERN_5, brand));
        allItems.addAll(parsePattern(text, PATTERN_4, brand));
        allItems.addAll(parsePattern(text, PATTERN_7, brand));

        repository.saveAll(filterDuplicateItems(allItems));

        return filterDuplicateItems(allItems);
    }

    private LocalDate convertDateStringToLocalDate(String dateString, String brand) {
        int currentYear = Year.now().getValue();

        if ("Hipercard".equals(brand)) {

            String[] dateParts = dateString.split("/");
            if (dateParts.length < 2) {
                return null;
            }
            String day = dateParts[0];
            String month = dateParts.length > 1 ? dateParts[1] : "01"; //OBS
            String dateWithYear = day + "/" + month + "/" + currentYear;

            return LocalDate.parse(dateWithYear, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else {
            String[] parts = dateString.split(" ");
            if (parts.length < 2) {
                return null;
            }
            String day = parts[0];
            String monthAbbr = parts[1];
            String month = MONTH_MAP.get(monthAbbr.toLowerCase());
            if (month == null) {
                return null;
            }
            String dateWithYear = day + "/" + month + "/" + currentYear;
            LocalDate parsedDate = LocalDate.parse(dateWithYear, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return parsedDate;
        }
    }



    private List<InvoiceItem> parsePattern(String text, Pattern pattern, String brand) {
        List<InvoiceItem> matchedItems = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {

            LocalDate purchaseDate = convertDateStringToLocalDate(matcher.group(1), brand);
            String establishment = matcher.group(2).trim();
            String installment = matcher.group(3) != null ? matcher.group(3).trim() : "N/A";
            String valorString = matcher.group(4);

            if ("PAGAMENTO FICHA COMPENS".equals(establishment)) {
                continue;
            }
            else if ("CREDITO PAGAMENTO LOJA".equals(establishment)) {
                continue;
            }
            else if ("Total dos lançamentos atuais".equals(establishment)) {
                continue;
            }

            valorString = valorString.replace('.', ',');

            int lastCommaIndex = valorString.lastIndexOf(',');
            if (lastCommaIndex != -1) {
                String beforeLastComma = valorString.substring(0, lastCommaIndex).replaceAll(",", "");
                String afterLastComma = valorString.substring(lastCommaIndex + 1);
                valorString = beforeLastComma + "." + afterLastComma;
            }

            Double value;
            try {
                value = Double.parseDouble(valorString);
            } catch (NumberFormatException e) {
                continue;
            }

            InvoiceItem item = new InvoiceItem();
            item.setBrand(brand);
            item.setPurchaseDate(purchaseDate);
            item.setEstablishment(establishment);
            item.setInstallment(installment);
            item.setItemValue(value);

            matchedItems.add(item);
        }
        return matchedItems;
    }

    private List<InvoiceItem> filterDuplicateItems(List<InvoiceItem> items) {
        Set<String> seen = new HashSet<>();
        List<InvoiceItem> uniqueItems = new ArrayList<>();

        for (InvoiceItem item : items) {
            String key = item.getItemValue() + "@" + item.getPurchaseDate();

            boolean isInvalidItem = item.getEstablishment().isEmpty() && "N/A".equals(item.getInstallment());

            if (!seen.contains(key) && !isInvalidItem) {
                seen.add(key);
                uniqueItems.add(item);
            }
        }
        return uniqueItems;
    }

    public Double sumInvoiceItemValues(List<InvoiceItem> items) {
        return items.stream()
                .map(InvoiceItem::getItemValue)
                .reduce(0.0, Double::sum);
    }
}

