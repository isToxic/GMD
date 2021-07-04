FROM gradle:7.1-jdk16-hotspot

RUN apt-get install git

RUN git clone https://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@github.com/isToxic/GMD.git /opt/GMD

CMD cd /opt/GMD && gradle bootRun --scan
