package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Lists.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.inmemorydao.InMemoryMenuDao;
import kitchenpos.inmemorydao.InMemoryMenuGroupDao;
import kitchenpos.inmemorydao.InMemoryMenuProductDao;
import kitchenpos.inmemorydao.InMemoryProductDao;

@DisplayName("MenuService 테스트")
class MenuServiceTest {
    private MenuDao menuDao;
    private MenuGroupDao menuGroupDao;
    private MenuProductDao menuProductDao;
    private ProductDao productDao;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        this.menuDao = new InMemoryMenuDao();
        this.menuGroupDao = new InMemoryMenuGroupDao();
        this.menuProductDao = new InMemoryMenuProductDao();
        this.productDao = new InMemoryProductDao();
        this.menuService = new MenuService(menuDao, menuGroupDao, menuProductDao, productDao);
    }

    @DisplayName("메뉴를 등록한다")
    @Test
    void create() {
        // Given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("뼈있는치킨");
        final MenuGroup savedMenuGroup = menuGroupDao.save(menuGroup);

        final Product product = new Product();
        product.setName("파닭치킨");
        product.setPrice(BigDecimal.valueOf(18000L));
        final Product savedProduct = productDao.save(product);

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(savedProduct.getId());
        menuProduct.setQuantity(1L);

        final Menu menu = new Menu();
        menu.setName("파닭치킨");
        menu.setPrice(BigDecimal.valueOf(18000L));
        menu.setMenuGroupId(savedMenuGroup.getId());
        menu.setMenuProducts(newArrayList(menuProduct));

        // When
        final Menu savedMenu = menuService.create(menu);

        // Then
        assertThat(savedMenu)
                .extracting(Menu::getId)
                .isNotNull()
        ;
    }

    @DisplayName("메뉴의 가격이 올바르지 않은 경우 예외가 발생한다")
    @ParameterizedTest
    @MethodSource("generateInvalidPrice")
    void create_InvalidPrice_ExceptionThrown(final BigDecimal price) {
        // When
        final Menu menu = new Menu();
        menu.setName("파닭치킨");
        menu.setPrice(price);

        // Then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
        ;
    }

    private static Stream<Arguments> generateInvalidPrice() {
        return Stream.of(
                Arguments.arguments((BigDecimal)null),
                Arguments.arguments(BigDecimal.valueOf(-1000L))
        );
    }

    @DisplayName("메뉴 그룹의 Id가 null일 경우 예외가 발생한다")
    @Test
    void create_MenuGroupIdIsNull_ExceptionThrown() {
        // Given
        final Menu menu = new Menu();
        menu.setName("파닭치킨");
        menu.setPrice(BigDecimal.valueOf(18000L));

        // Then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
        ;
    }

    @DisplayName("상품이 존재하지 않을 경우 예외가 발생한다")
    @Test
    void create_ProductNotExists_ExceptionThrown() {
        // Given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("뼈있는치킨");
        final MenuGroup savedMenuGroup = menuGroupDao.save(menuGroup);

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(1L); // 존재하지 않는 상품 Id
        menuProduct.setQuantity(1L);

        final Menu menu = new Menu();
        menu.setName("파닭치킨");
        menu.setPrice(BigDecimal.valueOf(18000L));
        menu.setMenuGroupId(savedMenuGroup.getId());
        menu.setMenuProducts(newArrayList(menuProduct));

        // Then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
        ;
    }

    @DisplayName("메뉴의 가격이 상품 가격의 합보다 클 경우 예외가 발생한다")
    @Test
    void create_PriceIsHigherThanSumOfProductPrice_ExceptionThrown() {
        // Given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("뼈있는치킨");
        final MenuGroup savedMenuGroup = menuGroupDao.save(menuGroup);

        final Product product = new Product();
        product.setName("파닭치킨");
        product.setPrice(BigDecimal.valueOf(18000L));
        final Product savedProduct = productDao.save(product);

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(savedProduct.getId());
        menuProduct.setQuantity(1L);

        final Menu menu = new Menu();
        menu.setName("파닭치킨");
        menu.setPrice(BigDecimal.valueOf(19000L));
        menu.setMenuGroupId(savedMenuGroup.getId());
        menu.setMenuProducts(newArrayList(menuProduct));

        // Then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class)
        ;
    }

    @DisplayName("메뉴의 목록을 조회한다")
    @Test
    void list() {
        // Given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("뼈있는치킨");
        final MenuGroup savedMenuGroup = menuGroupDao.save(menuGroup);

        final Product product = new Product();
        product.setName("파닭치킨");
        product.setPrice(BigDecimal.valueOf(18000L));
        final Product savedProduct = productDao.save(product);

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(savedProduct.getId());
        menuProduct.setQuantity(1L);

        final Menu menu = new Menu();
        menu.setName("파닭치킨");
        menu.setPrice(BigDecimal.valueOf(18000L));
        menu.setMenuGroupId(savedMenuGroup.getId());
        menu.setMenuProducts(newArrayList(menuProduct));
        menuService.create(menu);

        // When
        final List<Menu> list = menuService.list();

        // Then
        assertThat(list).isNotEmpty();
    }
}
