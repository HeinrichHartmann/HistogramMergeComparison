FROM jupyter/scipy-notebook

MAINTAINER Heinrich Hartmann <heinrich@heinrichhartmann.com>

USER root
RUN apt-get update
RUN apt-get upgrade -y
RUN apt-get install -y\
    emacs \
    tmux \
    curl \
    less \
    man \
    autoconf \
    strace

# install latest libcircllhist from sources
RUN git clone https://github.com/circonus-labs/libcircllhist/ /tmp/libcircllhist &&\
    cd /tmp/libcircllhist &&\
    autoconf &&\
    ./configure --libdir /usr/lib/x86_64-linux-gnu/ &&\
    make && make install

# Give $NB_USER passwordless sudo
RUN printf "$NB_USER\tALL=(ALL)\tNOPASSWD: ALL" > /etc/sudoers.d/$NB_USER

USER $NB_USER

RUN pip install --upgrade pip

RUN pip install \
    circonusapi \
    circllhist

# We will run java stuff
RUN sudo apt-get update
RUN sudo apt-get install -y default-jre

ADD tdigest-merge-tool.jar ./

EXPOSE 9998
EXPOSE 9999

ADD cmd.sh ./

CMD ["./cmd.sh"]
