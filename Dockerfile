FROM gradle:7.1-jdk16-hotspot

RUN locale-gen ru_RU.UTF-8
RUN update-locale LANG=ru_RU.UTF-8 LC_MESSAGES=POSIX

RUN apt-get update
RUN apt-get install git

RUN git clone https://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@github.com/isToxic/GMD.git /opt/GMD

CMD cd /opt/GMD && gradle bootRun --scan

#EXPOSE 80
#EXPOSE 443
#EXPOSE 587