import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class Specifications {
    public static RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()              // Внутри метода используется RequestSpecBuilder для построения спецификации
                .setBaseUri("https://reqres.in/")    // устанавливает стартовый URL для всех запросов
                .setRelaxedHTTPSValidation()         // отключает проверку сертификата для HTTPS соединений
                .setContentType(ContentType.JSON)    // устанавливает Content Type для отправляемых запросов как JSON
                .setAccept(ContentType.JSON)         // устанавливает Accept заголовок для ожидаемых ответов как JSON
                .build();
    }

    public static ResponseSpecification responseSpecificationScOK() {
        return new ResponseSpecBuilder()             // Внутри метода используется ResponseSpecBuilder для построения спецификации
                .log(LogDetail.STATUS)               // включает логирование статуса ответа
                .expectContentType(ContentType.JSON) // ожидает, что Content Type ответа будет JSON
                .expectStatusCode(HttpStatus.SC_OK)  // ожидает успешный статус код (200)
                .expectResponseTime(lessThanOrEqualTo(3L), SECONDS) // ожидает, что время ответа будет меньше или равно 3 секундам
                .build();
    }

    public static ResponseSpecification responseSpecUnique(int status) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status)
                .build();

    }
}
