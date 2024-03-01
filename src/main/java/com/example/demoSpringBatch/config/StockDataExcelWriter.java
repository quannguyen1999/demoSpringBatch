package com.example.demoSpringBatch.config;


import com.example.demoSpringBatch.models.ExcelDtoRequest;
import com.example.demoSpringBatch.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component("stockDataExcelWriter")
@Scope("step")
public class StockDataExcelWriter implements ItemWriter<Object[]> {

    private Workbook workbook;

    private Sheet sheet;

    public ExcelDtoRequest excelDtoRequest;

    private int currRow = 0;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) throws JsonProcessingException {
        String jsonRequest = stepExecution.getJobExecution().getJobParameters().getString("response");
        ExcelDtoRequest excelDtoRequestSave = Utils.convertToExcelDtoRequest(jsonRequest);

        workbook = new XSSFWorkbook();

        sheet = workbook.createSheet(excelDtoRequestSave.getNameSheet());

        addCells(excelDtoRequestSave.getListFields(), sheet);

        excelDtoRequest = excelDtoRequestSave;
    }


    @Override
    public void write(Chunk<? extends Object[]> chunk) throws Exception {
        sheet = workbook.getSheetAt(0);
        for (Object[] item : chunk.getItems()) {
            currRow++;
            Row row = sheet.createRow(currRow);
            for (int locateField = 0; locateField < excelDtoRequest.getListFields().size(); locateField++) {
                Cell cell1 = row.createCell(locateField);
                cell1.setCellValue(item[locateField].toString());
            }
        }
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) throws IOException {
        FileOutputStream fos = new FileOutputStream("test.xlsx");
        workbook.write(fos);
        fos.close();
    }

    private static void addCells(List<String> listFields, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < listFields.size(); i++) {
            Cell headerCell1 = headerRow.createCell(i);
            headerCell1.setCellValue(listFields.get(i));
        }
    }


}
