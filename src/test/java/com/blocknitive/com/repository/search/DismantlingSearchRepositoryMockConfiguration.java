package com.blocknitive.com.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link DismantlingSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class DismantlingSearchRepositoryMockConfiguration {

    @MockBean
    private DismantlingSearchRepository mockDismantlingSearchRepository;
}
