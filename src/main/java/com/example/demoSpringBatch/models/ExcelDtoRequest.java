package com.example.demoSpringBatch.models;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class ExcelDtoRequest {

    List<String> listFields;

    String nameSheet;

}
