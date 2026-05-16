package ai.chat2db.server.domain.api.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratorTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private String label;

    private String category;

    private String expression;

    private String example;

    private String suggestedDataType;

    public static GeneratorTemplate of(String label, String category, String expression, String example, String suggestedDataType) {
        return new GeneratorTemplate(label, category, expression, example, suggestedDataType);
    }

    public static List<GeneratorTemplate> getDefaultTemplates() {
        return List.of(
                GeneratorTemplate.of("UUID", "基础", "#{IdNumber.valid}", "a1b2c3d4-e5f6-7890-abcd-ef1234567890", "VARCHAR"),
                GeneratorTemplate.of("布尔值", "基础", "#{Options.option 'true','false'}", "true", "BOOLEAN"),

                GeneratorTemplate.of("姓", "姓名", "#{Name.last_name}", "Smith", "VARCHAR"),
                GeneratorTemplate.of("名", "姓名", "#{Name.first_name}", "John", "VARCHAR"),
                GeneratorTemplate.of("全名", "姓名", "#{Name.full_name}", "John Smith", "VARCHAR"),

                GeneratorTemplate.of("邮箱", "联系方式", "#{Internet.email_address}", "john@example.com", "VARCHAR"),
                GeneratorTemplate.of("手机号", "联系方式", "#{PhoneNumber.cell_phone}", "555-123-4567", "VARCHAR"),
                GeneratorTemplate.of("电话号码", "联系方式", "#{PhoneNumber.phone_number}", "555-123-4567", "VARCHAR"),
                GeneratorTemplate.of("用户名", "联系方式", "#{Internet.username}", "john123", "VARCHAR"),
                GeneratorTemplate.of("URL", "联系方式", "#{Internet.url}", "https://example.com", "VARCHAR"),
                GeneratorTemplate.of("IP 地址", "联系方式", "#{Internet.ip_v4_address}", "192.168.1.1", "VARCHAR"),
                GeneratorTemplate.of("MAC 地址", "联系方式", "#{Internet.mac_address}", "00:1A:2B:3C:4D:5E", "VARCHAR"),

                GeneratorTemplate.of("公司名", "商业", "#{Company.name}", "Acme Corp", "VARCHAR"),
                GeneratorTemplate.of("职位", "商业", "#{Job.title}", "Software Engineer", "VARCHAR"),
                GeneratorTemplate.of("部门", "商业", "#{Commerce.department}", "Electronics", "VARCHAR"),
                GeneratorTemplate.of("产品名", "商业", "#{Commerce.product_name}", "Ergonomic Chair", "VARCHAR"),
                GeneratorTemplate.of("价格", "商业", "#{Commerce.price}", "499.99", "DECIMAL"),

                GeneratorTemplate.of("国家", "地址", "#{Address.country}", "United States", "VARCHAR"),
                GeneratorTemplate.of("城市", "地址", "#{Address.city}", "New York", "VARCHAR"),
                GeneratorTemplate.of("省份", "地址", "#{Address.state}", "California", "VARCHAR"),
                GeneratorTemplate.of("街道", "地址", "#{Address.street_address}", "123 Main St", "VARCHAR"),
                GeneratorTemplate.of("邮编", "地址", "#{Address.zip_code}", "10001", "VARCHAR"),
                GeneratorTemplate.of("完整地址", "地址", "#{Address.full_address}", "123 Main St, New York, NY 10001", "VARCHAR"),

                GeneratorTemplate.of("生日", "日期时间", "#{Date.birthday}", "1990-01-15", "DATE"),
                GeneratorTemplate.of("过去时间", "日期时间", "#{Date.past '30','DAYS'}", "2024-01-10 14:30:00", "DATETIME"),
                GeneratorTemplate.of("未来时间", "日期时间", "#{Date.future '30','DAYS'}", "2024-02-20 09:15:00", "DATETIME"),

                GeneratorTemplate.of("单词", "文本", "#{Lorem.word}", "lorem", "VARCHAR"),
                GeneratorTemplate.of("句子", "文本", "#{Lorem.sentence}", "Lorem ipsum dolor sit amet.", "VARCHAR"),
                GeneratorTemplate.of("段落", "文本", "#{Lorem.paragraph}", "Lorem ipsum dolor...", "TEXT"),

                GeneratorTemplate.of("整数 0-100", "数值", "#{Number.number_between '0','100'}", "42", "INT"),
                GeneratorTemplate.of("整数 0-1000", "数值", "#{Number.number_between '0','1000'}", "756", "INT"),
                GeneratorTemplate.of("整数 0-10000", "数值", "#{Number.number_between '0','10000'}", "5432", "INT")
        );
    }
}
