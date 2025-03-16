# vim:set ft=dockerfile:
ARG BASE_IMAGE

FROM ${BASE_IMAGE}

ARG VERSION
ARG SCM_REF
ARG BUILD_TAG
ARG NUXEO_CLID
ARG PACKAGES

LABEL com.customer.sample.version=${VERSION}
LABEL com.customer.sample.scm-ref=${SCM_REF}
LABEL com.customer.sample.build-tag=${BUILD_TAG}

# We need to be root to run yum commands
USER 0

# Install repos
RUN dnf -y install epel-release && dnf -y install https://mirrors.rpmfusion.org/free/el/rpmfusion-free-release-9.noarch.rpm && dnf -y install https://rpms.remirepo.net/enterprise/remi-release-9.rpm
RUN dnf config-manager --enable ol9_codeready_builder

#Install ffmpeg and codecs
RUN dnf -y install mediainfo ffmpeg x264 x265 opus libvpx flac libvorbis-devel

# Set back original (nuxeo) user
USER 900

COPY --chown=nuxeo:0 target/nuxeo-dynamic-asset-transformation*.zip /home/nuxeo/local-packages/

RUN /install-packages.sh --offline /home/nuxeo/local-packages/nuxeo-dynamic-asset-transformation*.zip