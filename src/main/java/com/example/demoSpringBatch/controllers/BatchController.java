package com.example.demoSpringBatch.controllers;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @GetMapping("/excel")
    @ResponseBody
    public ResponseEntity<Resource> exportExcel() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("timestamp", String.valueOf(System.currentTimeMillis()))
                .addString("valueTest","dit con me no work kia moi nguoi")
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
//        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
//            ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
//            byte[] excelBytes = (byte[]) jobExecutionContext.get("excelFile");
//            ByteArrayResource resource = new ByteArrayResource(excelBytes);
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output.xlsx\"")
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .body(resource);
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(null);
//        }
        return ResponseEntity.status(HttpStatus.OK)
                    .body(null);
    }

    private byte[] getExcelBytes() throws IOException {
        // Code to generate Excel byte array from Spring Batch output
        // Example:
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");
        // Write data to the sheet
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}
