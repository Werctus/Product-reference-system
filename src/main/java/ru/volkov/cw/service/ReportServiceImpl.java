package ru.volkov.cw.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.volkov.cw.dao.ReportDAO;
import ru.volkov.cw.dao.PriceHistoryDAO;
import ru.volkov.cw.model.PriceHistory;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportServiceImpl implements ReportService {

    private final ReportDAO reportDAO;
    private final PriceHistoryDAO priceHistoryDAO;

    public ReportServiceImpl(ReportDAO reportDAO, PriceHistoryDAO priceHistoryDAO) {
        this.reportDAO = reportDAO;
        this.priceHistoryDAO = priceHistoryDAO;
    }

    @Override
    public void generateSuppliesReport() {
        System.out.println("Генерация отчета по поставкам...");
    }

    @Override
    public void generatePriceChangesReport() {
        System.out.println("Генерация отчета по изменению цен...");
    }

    @Override
    public void generateInventoryValuation() {
        System.out.println("Генерация оценки инвентаря...");
    }

    @Override
    public void generateAuditSummary() {
        System.out.println("Генерация сводного аудита...");
    }

    @Override
    public void generateSuppliesReportExcel(LocalDate fromDate, LocalDate toDate, File file) {
        System.out.println("📊 Генерация Excel-отчёта по поставкам с " + fromDate + " по " + toDate);

        // Получаем данные из БД
        List<String[]> reportData = reportDAO.getDeliveryReport(
                fromDate,
                toDate,
                null, // storeId - все магазины
                null, // companyId - все компании
                null  // categoryId - все категории
        );

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Отчёт по поставкам");

            // Создаем стили
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // Заголовок отчёта
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("ОТЧЁТ ПО ПОСТАВКАМ");
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            // Период
            Row periodRow = sheet.createRow(1);
            Cell periodCell = periodRow.createCell(0);
            periodCell.setCellValue("Период: " + fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                    " - " + toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            // Шапка таблицы
            String[] headers = {"Дата", "№ документа", "Поставщик", "Магазин", "Товар", "Категория", "Количество", "Сумма"};
            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Данные
            int rowNum = 4;
            double totalAmount = 0;
            int totalQuantity = 0;

            for (String[] row : reportData) {
                Row dataRow = sheet.createRow(rowNum++);

                // Дата
                Cell dateCell = dataRow.createCell(0);
                dateCell.setCellValue(row[0]); // delivery_date
                dateCell.setCellStyle(dateStyle);

                // Номер документа
                dataRow.createCell(1).setCellValue(row[1]); // document_number

                // Поставщик
                dataRow.createCell(2).setCellValue(row[2]); // company_name

                // Магазин
                dataRow.createCell(3).setCellValue(row[3]); // store_name

                // Товар
                dataRow.createCell(4).setCellValue(row[4]); // product_name

                // Категория
                dataRow.createCell(5).setCellValue(row[5]); // category_name

                // Количество
                int qty = Integer.parseInt(row[6]);
                Cell qtyCell = dataRow.createCell(6);
                qtyCell.setCellValue(qty);
                qtyCell.setCellStyle(numberStyle);
                totalQuantity += qty;

                // Сумма
                double amount = Double.parseDouble(row[7]);
                Cell amountCell = dataRow.createCell(7);
                amountCell.setCellValue(amount);
                amountCell.setCellStyle(currencyStyle);
                totalAmount += amount;
            }

            // Итоговая строка
            Row totalRow = sheet.createRow(rowNum++);
            totalRow.createCell(0).setCellValue("ИТОГО:");
            Cell totalQtyCell = totalRow.createCell(6);
            totalQtyCell.setCellValue(totalQuantity);
            totalQtyCell.setCellStyle(numberStyle);
            Cell totalAmtCell = totalRow.createCell(7);
            totalAmtCell.setCellValue(totalAmount);
            totalAmtCell.setCellStyle(currencyStyle);

            // Автоширина колонок
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            // Сохраняем файл
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            System.out.println("Отчёт сохранён: " + file.getAbsolutePath());
            System.out.println("Всего записей: " + reportData.size());
            System.out.println("Общая сумма: " + totalAmount);

        } catch (IOException e) {
            System.err.println("Ошибка создания Excel-файла: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось создать Excel-файл: " + e.getMessage(), e);
        }
    }

    @Override
    public void generatePriceChangesReportExcel(LocalDate fromDate, LocalDate toDate, File file) {
        System.out.println("Генерация Excel-отчёта по изменению цен с " + fromDate + " по " + toDate);

        // Получаем историю цен
        var priceHistoryList = priceHistoryDAO.getPriceHistoryByDateRange(
                fromDate.atStartOfDay(),
                toDate.atTime(23, 59, 59)
        );

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("История изменения цен");

            // Создаем стили
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateTimeStyle = createDateTimeStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle percentStyle = createPercentStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // Заголовок отчёта
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("ОТЧЁТ ПО ИЗМЕНЕНИЮ ЦЕН");
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            // Период
            Row periodRow = sheet.createRow(1);
            Cell periodCell = periodRow.createCell(0);
            periodCell.setCellValue("Период: " + fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                    " - " + toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

            // Шапка таблицы
            String[] headers = {"Дата и время", "Товар", "Старая цена", "Новая цена", "Изменение", "Изменение %"};
            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Данные
            int rowNum = 4;
            int changesCount = 0;
            double totalIncrease = 0;
            double totalDecrease = 0;

            for (PriceHistory history : priceHistoryList) {
                Row dataRow = sheet.createRow(rowNum++);

                // Дата и время изменения
                Cell dateTimeCell = dataRow.createCell(0);
                if (history.getChangedAt() != null) {
                    dateTimeCell.setCellValue(history.getChangedAt());
                }
                dateTimeCell.setCellStyle(dateTimeStyle);

                // Товар
                dataRow.createCell(1).setCellValue(
                        history.getProductName() != null ? history.getProductName() : "ID: " + history.getProductId()
                );

                // Старая цена
                Cell oldPriceCell = dataRow.createCell(2);
                if (history.getOldPrice() != null) {
                    oldPriceCell.setCellValue(history.getOldPrice().doubleValue());
                    oldPriceCell.setCellStyle(currencyStyle);
                }

                // Новая цена
                Cell newPriceCell = dataRow.createCell(3);
                if (history.getNewPrice() != null) {
                    newPriceCell.setCellValue(history.getNewPrice().doubleValue());
                    newPriceCell.setCellStyle(currencyStyle);
                }

                // Изменение (разница)
                Cell changeCell = dataRow.createCell(4);
                if (history.getOldPrice() != null && history.getNewPrice() != null) {
                    double change = history.getNewPrice().doubleValue() - history.getOldPrice().doubleValue();
                    changeCell.setCellValue(change);
                    changeCell.setCellStyle(currencyStyle);

                    if (change > 0) totalIncrease += change;
                    else totalDecrease += Math.abs(change);

                    changesCount++;
                }

                // Изменение в процентах - ИСПРАВЛЕНО
                Cell percentCell = dataRow.createCell(5);
                if (history.getOldPrice() != null && history.getNewPrice() != null &&
                        history.getOldPrice().doubleValue() != 0) {
                    double oldPrice = history.getOldPrice().doubleValue();
                    double newPrice = history.getNewPrice().doubleValue();
                    // Записываем десятичное значение, а не процент
                    double percentDecimal = (newPrice - oldPrice) / oldPrice;
                    percentCell.setCellValue(percentDecimal);
                    percentCell.setCellStyle(percentStyle);
                }
            }

            // Итоговая строка
            Row totalRow = sheet.createRow(rowNum++);
            totalRow.createCell(0).setCellValue("ИТОГО:");
            totalRow.createCell(1).setCellValue("Всего изменений: " + changesCount);
            Cell increaseCell = totalRow.createCell(3);
            increaseCell.setCellValue("↑ +" + totalIncrease);
            increaseCell.setCellStyle(currencyStyle);
            Cell decreaseCell = totalRow.createCell(4);
            decreaseCell.setCellValue("↓ -" + totalDecrease);
            decreaseCell.setCellStyle(currencyStyle);

            // Автоширина колонок
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            // Сохраняем файл
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            System.out.println("Отчёт сохранён: " + file.getAbsolutePath());
            System.out.println("Всего изменений цен: " + priceHistoryList.size());

        } catch (IOException e) {
            System.err.println("Ошибка создания Excel-файла: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось создать Excel-файл: " + e.getMessage(), e);
        }
    }

    // Вспомогательные методы для создания стилей
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDateTimeStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd.mm.yyyy hh:mm"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd.mm.yyyy hh:mm"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00 ₽"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createPercentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("0.00%"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
}