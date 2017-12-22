#FROM docker.elastic.co/elasticsearch/elasticsearch:5.5.2
FROM docker.elastic.co/elasticsearch/elasticsearch:5.6.0
USER root
ADD elasticsearch.yml /usr/share/elasticsearch/config/
RUN chown elasticsearch:elasticsearch config/elasticsearch.yml
RUN bin/elasticsearch-plugin install analysis-phonetic
USER elasticsearch
