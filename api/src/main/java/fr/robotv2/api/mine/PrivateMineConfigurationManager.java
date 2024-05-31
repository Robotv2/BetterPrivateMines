package fr.robotv2.api.mine;

import java.util.Collection;

public interface PrivateMineConfigurationManager<T> {
    PrivateMineConfiguration<T> getConfiguration(String configName);
    Collection<PrivateMineConfiguration<T>> getConfigurations();
}
