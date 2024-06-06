FROM phyremaster/papermc

# Make sure the plugin folder is created
RUN mkdir -p /papermc/plugins

COPY dist/target/BetterPrivateMines.jar /papermc/plugins/BetterPrivateMines.jar

