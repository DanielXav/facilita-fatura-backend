package com.danielxavier.FacilitaFatura.services;


import com.danielxavier.FacilitaFatura.entities.InvoiceItem;
import com.danielxavier.FacilitaFatura.repositories.InvoiceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        return filterDuplicateItems(allItems);
    }


    private List<InvoiceItem> parsePattern(String text, Pattern pattern, String brand) {
        List<InvoiceItem> matchedItems = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        int currentYear = Year.now().getValue();

        while (matcher.find()) {
            String dateWithYear = matcher.group(1) + "/" + currentYear;

            String[] dateParts = dateWithYear.split("/");

            if (dateParts[0].equals("00")) {
                dateWithYear = "01/" + dateParts[1] + "/" + dateParts[2];
            }

            LocalDate purchaseDate = LocalDate.parse(dateWithYear, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String establishment = matcher.group(2).trim();
            String installment = matcher.group(3) != null ? matcher.group(3).trim() : "N/A";
            String valorString = matcher.group(4);

            if ("PAGAMENTO FICHA COMPENS".equals(establishment)) {
                continue;
            }
            else if ("CREDITO PAGAMENTO LOJA".equals(establishment)) {
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
            item.setValue(value);

            matchedItems.add(item);
        }
        return matchedItems;
    }

    private List<InvoiceItem> filterDuplicateItems(List<InvoiceItem> items) {
        Set<String> seen = new HashSet<>();
        List<InvoiceItem> uniqueItems = new ArrayList<>();

        for (InvoiceItem item : items) {
            String key = item.getValue() + "@" + item.getPurchaseDate();

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
                .map(InvoiceItem::getValue)
                .reduce(0.0, Double::sum);
    }


}

