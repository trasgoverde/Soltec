package com.blocknitive.com.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ProvidersSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ProvidersSearchRepositoryMockConfiguration {

    @MockBean
    private ProvidersSearchRepository mockProvidersSearchRepository;
}
