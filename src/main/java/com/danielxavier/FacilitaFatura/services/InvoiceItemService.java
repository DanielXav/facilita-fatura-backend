package com.danielxavier.FacilitaFatura.services;


import com.danielxavier.FacilitaFatura.entities.InvoiceItem;
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

    private static final Pattern PATTERN_1 = Pattern.compile(
            "\\@?(\\d{2}/\\d{2})\\s*(.*?)\\s*(\\d{2}/\\d{2})\\s*\\n?(\\d{1,3}(\\.\\d{3})*,\\d{2}|\\d+\\.\\d{2})"
    );

    private static final Pattern PATTERN_2 = Pattern.compile(
            "\\@?(\\d{2}/\\d{2})\\s*\\n(.*?)\\s*(\\d{2}/\\d{2})\\s*(\\d{1,3}(\\.\\d{3})*,\\d{2}|\\d+\\.\\d{2})"
    );

    private static final Pattern PATTERN_3 = Pattern.compile(
            "(\\d{2}/\\d{2})\\s*(.*?)\\s*(\\d{2}/\\d{2})\\s*\\n(\\d{1,3}(\\.\\d{3})*,\\d{2}|\\d+\\.\\d{2})"
    );

    private static final Pattern PATTERN_4 = Pattern.compile(
            "(\\d{2}/\\d{2})\\s*\\n(.*?)\\s*(\\d{2}/\\d{2})\\s*\\n(\\d{1,3}(\\.\\d{3})*,\\d{2}|\\d+\\.\\d{2})"
    );

    private static final Pattern PATTERN_5 = Pattern.compile(
            "(\\d{2}/\\d{2})\\s*\\n(.*?)\\s*\\n(\\d{2}/\\d{2})\\s*(\\d{1,3}(\\.\\d{3})*,\\d{2}|\\d+\\.\\d{2})"
    );

    private static final Pattern PATTERN_6 = Pattern.compile(
            "(\\d{2}/\\d{2})\\s*\\n(.*?)\\s*(\\d{2}/\\d{2})\\s*\\n(\\d{1,3}(\\.\\d{3})*,\\d{2}|\\d+\\.\\d{2})"
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
        allItems.addAll(parsePattern(text, PATTERN_4, brand));
        allItems.addAll(parsePattern(text, PATTERN_5, brand));
        allItems.addAll(parsePattern(text, PATTERN_6, brand));

        // Filtre os itens duplicados após coletar todos eles
        return filterDuplicateItems(allItems);
    }


    private List<InvoiceItem> parsePattern(String text, Pattern pattern, String brand) {
        List<InvoiceItem> matchedItems = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        int currentYear = Year.now().getValue();

        while (matcher.find()) {
            String dateWithYear = matcher.group(1) + "/" + currentYear;
            LocalDate purchaseDate = LocalDate.parse(dateWithYear, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String establishment = matcher.group(2).trim();
            String installment = matcher.group(3).trim();
            String valorString = matcher.group(4);

            if (valorString.matches("\\d{1,3},\\d{3}\\.\\d{2}")) {
                // Correção: apenas remove a vírgula, mantém o ponto
                valorString = valorString.replace(",", "");
            }


            // Remove pontos que são usados como separadores de milhares
            valorString = valorString.replaceAll("\\.(\\d{3})", "$1");

            // Substitui qualquer vírgula restante por um ponto
            valorString = valorString.replace(",", ".");

            BigDecimal value;
            try {
                value = new BigDecimal(valorString);
            } catch (NumberFormatException e) {
                continue;
            }

            // Verificar se o valor está no formato X.XX
            if (value.scale() == 2 && value.precision() - value.scale() == 1) {
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
        Set<BigDecimal> seenValues = new HashSet<>();
        List<InvoiceItem> uniqueItems = new ArrayList<>();

        for (InvoiceItem item : items) {
            if (!seenValues.contains(item.getValue())) {
                seenValues.add(item.getValue());
                uniqueItems.add(item);
            }
        }

        return uniqueItems;
    }

    public BigDecimal sumInvoiceItemValues(List<InvoiceItem> items) {
        return items.stream()
                .map(InvoiceItem::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}

