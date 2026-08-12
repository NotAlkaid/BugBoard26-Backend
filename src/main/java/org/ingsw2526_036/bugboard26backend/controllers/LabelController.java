package org.ingsw2526_036.bugboard26backend.controllers;

import java.util.List;

import org.ingsw2526_036.bugboard26backend.dtos.LabelRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.LabelResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Label;
import org.ingsw2526_036.bugboard26backend.mappers.LabelMapper;
import org.ingsw2526_036.bugboard26backend.services.LabelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@RestController
@RequestMapping("/api/labels")
@AllArgsConstructor
@Validated
public class LabelController {

    private final LabelService labelService;
    private final LabelMapper labelMapper;

    @PostMapping
    public ResponseEntity<@NonNull LabelResponseDto> createLabel(@Valid @RequestBody LabelRequestDto dto) {
        Label label = labelService.createLabel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(labelMapper.toDto(label));
    }

    @GetMapping
    public ResponseEntity<@NonNull List<LabelResponseDto>> getAllLabels() {
        List<Label> labels = labelService.getAllLabels();
        List<LabelResponseDto> dtos = labels.stream().map(labelMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull LabelResponseDto> getLabelById(@PathVariable Long id) {
        Label label = labelService.getLabelById(id);
        return ResponseEntity.ok(labelMapper.toDto(label));
    }

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull LabelResponseDto> updateLabel(@PathVariable Long id,
                                                                 @Valid @RequestBody LabelRequestDto dto) {
        Label updatedLabel = labelService.updateLabel(id, dto);
        return ResponseEntity.ok(labelMapper.toDto(updatedLabel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
        return ResponseEntity.noContent().build();
    }
}
