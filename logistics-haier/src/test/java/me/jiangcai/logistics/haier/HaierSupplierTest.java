package me.jiangcai.logistics.haier;

import me.jiangcai.logistics.LogisticsDestination;
import me.jiangcai.logistics.LogsticsTest;
import me.jiangcai.logistics.Thing;
import me.jiangcai.logistics.entity.Distribution;
import me.jiangcai.logistics.entity.Product;
import me.jiangcai.logistics.option.LogisticsOptions;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = HaierConfig.class)
public class HaierSupplierTest extends LogsticsTest {

    @Autowired
    private HaierSupplier haierSupplier;

    @Test
    public void product() {
        haierSupplier.updateProduct(randomProduct());
    }

    private Product randomProduct() {
        Product product = new Product();
        product.setCode(RandomStringUtils.randomAlphabetic(6));
        product.setName(RandomStringUtils.randomAlphabetic(3) + "名称");
        product.setBrand(RandomStringUtils.randomAlphabetic(3) + "品牌");
        product.setMainCategory(RandomStringUtils.randomAlphabetic(3) + "类目");
        product.setDescription(RandomStringUtils.randomAlphabetic(10));
        product.setSKU(RandomStringUtils.randomAlphabetic(69));
        product.setUnit(RandomStringUtils.randomAlphabetic(1));
        product.setVolumeHeight(new BigDecimal(random.nextInt(100) + 10));
        product.setVolumeLength(new BigDecimal(random.nextInt(100) + 10));
        product.setVolumeWidth(new BigDecimal(random.nextInt(100) + 10));
        product.setWeight(new BigDecimal(random.nextInt(3000) + 500));
        return product;
    }

    @Test
    public void sign() throws UnsupportedEncodingException, DecoderException {
        String content = "\uFEFF{\"sourcesn\":\"227e2ba80afa42c9b5fab7e334de8d48\",\"busflag\":\"1\",\"orderno\":\"227e2ba80afa42c9b5fab7e334de8d48\",\"city\":\"pur市\",\"county\":\"VgV区\",\"mobile\":\"17694614718\",\"orderdate\":\"2017-07-24 16:01:08\",\"bustype\":\"2\",\"storecode\":\"kV9E\",\"province\":\"YHf省\",\"name\":\"AhX人\",\"expno\":\"227e2ba80afa42c9b5fab7e334de8d48\",\"addr\":\"FTFHGg\",\"items\":[{\"number\":5,\"productcode\":\"3jjecj\",\"itemno\":1,\"prodes\":\"wMjN产品\",\"storagetype\":\"10\"},{\"number\":7,\"productcode\":\"UyYIBw\",\"itemno\":2,\"prodes\":\"wmtP产品\",\"storagetype\":\"10\"}],\"ordertype\":\"3\"}";
        String keyValue = "RRS,123";

        final String hex = DigestUtils.md5Hex(content + keyValue);
        System.out.println(content + keyValue);
        System.out.println(hex);
        System.out.println(Base64.getEncoder().encodeToString(hex.getBytes()));
        //只有生成出来的为 597221d49e3195fc1e7f2420dd678b47 才可以满足要求
//        System.out.println(Base64.getEncoder().encodeToString("597221d49e3195fc1e7f2420dd678b47".getBytes()));

        assertThat(haierSupplier.sign(content, keyValue))
                .isEqualTo("ZTQwYWU1N2MwZDgzNzc3ZTVmOTgyNzY1N2UyY2Y5ZTU=");
    }

    // 临时入库
    @Test
    public void tempIn() {
        Set<Thing> goods = new HashSet<>();
        // uXkelZ和KWkLZc
        goods.add(newTempThing("KWkLZc"));
        goods.add(newTempThing("uXkelZ"));
        Distribution distribution = haierSupplier.makeDistributionOrder(randomStorage(), goods, randomDestination(), LogisticsOptions.CargoToStorage, null);
        System.out.println(distribution.getId());
        // 在日日顺实现中 如果是入库的话，实现是 目的，来源
        // 这个是存在极大问题的！
    }

    private Thing newTempThing(String code) {
        return new Thing() {
            @Override
            public String getProductCode() {
                return code;
            }

            @Override
            public String getProductName() {
                return code;
            }

            @Override
            public int getAmount() {
                return 500;
            }
        };
    }

    @Test
    public void go() {
        Set<Thing> goods = new HashSet<>();
        // uXkelZ和KWkLZc
        goods.add(newTempThing("KWkLZc"));
        goods.add(newTempThing("uXkelZ"));
        Distribution distribution = haierSupplier.makeDistributionOrder(randomStorage(), goods, randomDestination(), LogisticsOptions.Installation | LogisticsOptions.CargoFromStorage, null);
        haierSupplier.cancelOrder(distribution.getId(), true, null);
    }

    private LogisticsDestination randomDestination() {
        return new LogisticsDestination() {
//            @Override
//            public String getProvince() {
//                return RandomStringUtils.randomAlphabetic(3) + "省";
//            }

//            @Override
//            public String getCity() {
//                return RandomStringUtils.randomAlphabetic(3) + "市";
//            }

//            @Override
//            public String getCountry() {
//                return RandomStringUtils.randomAlphabetic(3) + "区";
//            }

            @Override
            public String getProvince() {
                return "北京市";
            }

            @Override
            public String getCity() {
                return "北京市";
            }

            @Override
            public String getCountry() {
                return "昌平区";
            }

            @Override
            public String getDetailAddress() {
                return RandomStringUtils.randomAlphabetic(6);
            }

            @Override
            public String getConsigneeName() {
                return RandomStringUtils.randomAlphabetic(3) + "人";
            }

            @Override
            public String getConsigneeMobile() {
                return randomMobile();
            }
        };
    }

    private Collection<Thing> randomThings() {
        final Stream.Builder<Thing> builder = Stream
                .builder();
        int x = random.nextInt(2) + 1;
        while (x-- > 0)
            builder.add(randomThing());
        return builder.build()
                .collect(Collectors.toSet());
    }

    private Thing randomThing() {
        return new Thing() {
            @Override
            public String getProductCode() {
                return RandomStringUtils.randomAlphanumeric(6);
            }

            @Override
            public String getProductName() {
                return RandomStringUtils.randomAlphabetic(4) + "产品";
            }

            @Override
            public int getAmount() {
                return random.nextInt(10) + 1;
            }
        };
    }

    private Storage randomStorage() {
        return new Storage() {
            @Override
            public String getStorageCode() {
//                return RandomStringUtils.randomAlphanumeric(4);
                return "C12101";
            }
        };
    }

}