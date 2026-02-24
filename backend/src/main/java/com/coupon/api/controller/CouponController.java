package com.coupon.api.controller;

import com.coupon.api.application.usecase.CreateCouponUseCase;
import com.coupon.api.application.usecase.DeleteCouponUseCase;
import com.coupon.api.application.usecase.GetCouponByIdUseCase;
import com.coupon.api.dto.CouponRequestDTO;
import com.coupon.api.dto.CouponResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@Api(tags = "Cupons", description = "Criar, buscar por ID e excluir cupons (soft delete).")
@CrossOrigin(origins = "*")
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final GetCouponByIdUseCase getCouponByIdUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;

    public CouponController(CreateCouponUseCase createCouponUseCase,
                           GetCouponByIdUseCase getCouponByIdUseCase,
                           DeleteCouponUseCase deleteCouponUseCase) {
        this.createCouponUseCase = createCouponUseCase;
        this.getCouponByIdUseCase = getCouponByIdUseCase;
        this.deleteCouponUseCase = deleteCouponUseCase;
    }

    @PostMapping
    @ApiOperation(value = "Create a new coupon", response = CouponResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Coupon created successfully"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 422, message = "Validation failed")
    })
    public ResponseEntity<CouponResponseDTO> createCoupon(@Valid @RequestBody CouponRequestDTO requestDTO) {
        CouponResponseDTO response = createCouponUseCase.execute(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get coupon by ID", response = CouponResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Coupon found"),
            @ApiResponse(code = 404, message = "Coupon not found")
    })
    public ResponseEntity<CouponResponseDTO> getCouponById(@PathVariable UUID id) {
        CouponResponseDTO response = getCouponByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a coupon (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Coupon deleted successfully"),
            @ApiResponse(code = 404, message = "Coupon not found"),
            @ApiResponse(code = 400, message = "Coupon already deleted")
    })
    public ResponseEntity<Void> deleteCoupon(@PathVariable UUID id) {
        deleteCouponUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

}
