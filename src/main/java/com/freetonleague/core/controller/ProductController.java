package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.product.ProductDto;
import com.freetonleague.core.domain.enums.ProductStatusType;
import com.freetonleague.core.service.RestProductFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping(path = ProductController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Product Management Controller")
public class ProductController {

    public static final String BASE_PATH = "/api/product";
    public static final String PATH_CREATE = "/";
    public static final String PATH_EDIT = "/";
    public static final String PATH_GET = "/{product_id}";
    public static final String PATH_DELETE = "/{product_id}";
    public static final String PATH_GET_LIST = "/list";

    private final RestProductFacade restProductFacade;

    @ApiOperation("Get product info by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<ProductDto> getProductById(@PathVariable("product_id") long id) {
        return new ResponseEntity<>(restProductFacade.getProduct(id), HttpStatus.OK);
    }

    @ApiOperation("Get product list info")
    @ApiPageable
    @GetMapping(path = PATH_GET_LIST)
    public ResponseEntity<Page<ProductDto>> getProductList(@PageableDefault Pageable pageable,
                                                           @RequestParam(value = "creator", required = false) String creatorLeagueId,
                                                           @RequestParam(value = "statuses", required = false) ProductStatusType... statuses) {
        List<ProductStatusType> statusList = nonNull(statuses) ? List.of(statuses) : null;
        return new ResponseEntity<>(restProductFacade.getProductList(pageable, creatorLeagueId, statusList), HttpStatus.OK);
    }

    @ApiOperation("Create new product on platform")
    @PostMapping(path = PATH_CREATE)
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        return new ResponseEntity<>(restProductFacade.addProduct(productDto), HttpStatus.CREATED);
    }

    @ApiOperation("Modify product on platform")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<ProductDto> modifyProduct(@RequestBody ProductDto productDto) {
        return new ResponseEntity<>(restProductFacade.editProduct(productDto), HttpStatus.OK);
    }

    @ApiOperation("Delete (mark) product on platform")
    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable("product_id") long id) {
        return new ResponseEntity<>(restProductFacade.deleteProduct(id), HttpStatus.OK);
    }
}
