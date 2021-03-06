package kitchenpos.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;

@RestController
public class ProductRestController {
    public static final String PRODUCT_REST_API_URI = "/api/products";

    private final ProductService productService;

    public ProductRestController(final ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(PRODUCT_REST_API_URI)
    public ResponseEntity<Product> create(@RequestBody final Product product) {
        final Product created = productService.create(product);
        final URI uri = URI.create("/api/products/" + created.getId());
        return ResponseEntity.created(uri)
                .body(created)
                ;
    }

    @GetMapping(PRODUCT_REST_API_URI)
    public ResponseEntity<List<Product>> list() {
        return ResponseEntity.ok()
                .body(productService.list())
                ;
    }
}
