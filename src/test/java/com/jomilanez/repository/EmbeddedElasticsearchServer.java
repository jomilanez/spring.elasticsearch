package com.jomilanez.repository;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

import java.io.IOException;
import java.nio.file.Paths;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

class EmbeddedElasticsearchServer {

    private static final String DEFAULT_DATA_DIRECTORY = "target/elasticsearch-data";

    private final Node node;
    private final String dataDirectory;

    EmbeddedElasticsearchServer() {
        this(DEFAULT_DATA_DIRECTORY);
    }

    private EmbeddedElasticsearchServer(String dataDirectory) {
        this.dataDirectory = dataDirectory;

        Settings.Builder elasticsearchSettings = Settings.settingsBuilder()
                .put("http.enabled", "false")
                .put("path.home", dataDirectory);

        node = nodeBuilder()
                .local(true)
                .settings(elasticsearchSettings.build())
                .node();
    }

    Client getClient() {
        return node.client();
    }

    void shutdown() {
        node.close();
        deleteDataDirectory();
    }

    private void deleteDataDirectory() {
        try {
            FileSystemUtils.deleteSubDirectories(Paths.get(dataDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete data directory of embedded elasticsearch server", e);
        }
    }
}
