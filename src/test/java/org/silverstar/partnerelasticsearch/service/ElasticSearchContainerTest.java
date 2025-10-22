package org.silverstar.partnerelasticsearch.service;

import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

@ActiveProfiles("test")
@Testcontainers
@DataElasticsearchTest
class ElasticSearchContainerTest {

    @Container
    @ServiceConnection // Spring의 ElasticsearchClient을 생성하기 위해 필요
    static private ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.9.0")
                    .withEnv("discovery.type", "single-node")
                    .withEnv("xpack.security.enabled", "false"); // 보안 설정 해제 (없으면 connection refused 발생)

    @Autowired
    protected ElasticsearchClient beanClient;

    // 데이터 초기화 시 필요한 ElasticsearchClient
    static protected ElasticsearchClient client;

    // 데이터 초기화 시 필요한 ElasticsearchClient 설정
    @BeforeAll
    static void setupClient() {
        // 컨테이너가 준비될 때까지 기다림
        elasticsearchContainer.waitingFor(Wait.forHttp("/").forStatusCode(200));

        RestClient restClient = RestClient.builder(
                HttpHost.create(elasticsearchContainer.getHttpHostAddress())
        ).build();

        client = new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
    }

    @Test
    void testDatabaseIsRunning() {
        assertThat(elasticsearchContainer.isRunning()).isTrue();
    }

    @Test
    void testClientIsRunning() throws Exception {
        InfoResponse info = client.info();
        assertThat(info.clusterName()).isNotNull();
    }

    // 번외 테스트
    @Autowired
    private ApplicationContext context;

    @Test
    void testClientsAreDifferent() {
        ElasticsearchClient staticClient = client;
        ElasticsearchClient serviceClientWithTest = beanClient;
        ElasticsearchClient serviceClient = context.getBean(ElasticsearchClient.class);

        assertNotSame(staticClient, serviceClient);
        assertSame(serviceClientWithTest, serviceClient);
    }

}
