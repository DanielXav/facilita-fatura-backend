package com.danielxavier.FacilitaFatura.services;


import com.danielxavier.FacilitaFatura.dto.ClientDTO;
import com.danielxavier.FacilitaFatura.dto.InvoiceItemDTO;
import com.danielxavier.FacilitaFatura.entities.Invoice;
import com.danielxavier.FacilitaFatura.entities.InvoiceItem;
import com.danielxavier.FacilitaFatura.entities.Client;
import com.danielxavier.FacilitaFatura.exceptions.DatabaseException;
import com.danielxavier.FacilitaFatura.exceptions.ResourceNotFoundException;
import com.danielxavier.FacilitaFatura.repositories.ClientRepository;
import com.danielxavier.FacilitaFatura.repositories.InvoiceItemRepository;
import com.danielxavier.FacilitaFatura.repositories.InvoiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    // Funções necessárias para pegar padrões dos itens das faturas e editar corretamente as Strings para guardar no banco nos devidos atributos.

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

    public List<InvoiceItemDTO> parseInvoiceItems(Long invoiceId, String text, String brand) {
        List<InvoiceItem> allItems = new ArrayList<>();
        List<InvoiceItemDTO> allItemsDTO = new ArrayList<>();

        allItems.addAll(parsePattern(invoiceId, text, PATTERN_1, brand));
        allItems.addAll(parsePattern(invoiceId, text, PATTERN_2, brand));
        allItems.addAll(parsePattern(invoiceId, text, PATTERN_3, brand));
        allItems.addAll(parsePattern(invoiceId, text, PATTERN_6, brand));
        allItems.addAll(parsePattern(invoiceId, text, PATTERN_5, brand));
        allItems.addAll(parsePattern(invoiceId, text, PATTERN_4, brand));
        allItems.addAll(parsePattern(invoiceId, text, PATTERN_7, brand));

        List<InvoiceItem> filteredItems = filterDuplicateItems(allItems);
        repository.saveAll(filteredItems);

        for (InvoiceItem item : filteredItems) {
            InvoiceItemDTO dto = new InvoiceItemDTO();
            BeanUtils.copyProperties(item, dto);
            allItemsDTO.add(dto);
        }


        return allItemsDTO;
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



    private List<InvoiceItem> parsePattern(Long invoiceId, String text, Pattern pattern, String brand) {
        List<InvoiceItem> matchedItems = new ArrayList<>();

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice não encontrado com ID: " + invoiceId));

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
            item.setInvoice(invoice);

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

    public Double sumInvoiceItemValues(List<InvoiceItemDTO> items) {
        return items.stream()
                .map(InvoiceItemDTO::getItemValue)
                .reduce(0.0, Double::sum);
    }

    // Restante do CRUD

    @Transactional(readOnly = true)
    public Page<InvoiceItemDTO> findAllPaged(Pageable pageable){
        Page<InvoiceItem> list = repository.findAll(pageable);
        return list.map(InvoiceItemDTO::new);
    }

    @Transactional(readOnly = true)
    public InvoiceItemDTO findById(Long id){
        InvoiceItem entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Id não encontrado " + id));
        return new InvoiceItemDTO(entity);
    }

    @Transactional
    public void assignClientToInvoiceItem(Long invoiceItemId, Long newClientId) {
        InvoiceItem item = repository.findById(invoiceItemId)
                .orElseThrow(() -> new ResourceNotFoundException("InvoiceItem não encontrado com ID: " + invoiceItemId));

        // Guarda o ID do cliente antigo, se houver
        Long oldClientId = item.getClient() != null ? item.getClient().getId() : null;

        // Associa o novo cliente
        Client newClient = clientRepository.findById(newClientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + newClientId));

        item.setClient(newClient);
        repository.save(item);

        // Recalcula o total para o cliente anterior, se houver
        if (oldClientId != null) {
            clientService.recalculateClientTotal(oldClientId);
        }

        // Recalcula o total para o novo cliente
        clientService.recalculateClientTotal(newClientId);
    }

    @Transactional
    public InvoiceItemDTO update(Long id, InvoiceItemDTO dto) {
        try {
            InvoiceItem entity = repository.getReferenceById(id);
            BeanUtils.copyProperties(dto, entity, "id");
            entity = repository.save(entity);
            return new InvoiceItemDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado " + id);
        }
    }

    public void delete(Long id) {
        if (!repository.existsById(id)){
            throw new ResourceNotFoundException("Item não encontrado!");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha na integridade referencial");
        }
    }
}

