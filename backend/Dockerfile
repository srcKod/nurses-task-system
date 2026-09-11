FROM php:8.1.0-fpm

# Copy composer.lock and composer.json package.json package-lock.json
COPY composer.lock composer.json package.json package-lock.json /var/www/example.com/

# Set working directory
WORKDIR /var/www/example.com

USER root

# Install dependencies
RUN \
    --mount=type=cache,target=/var/cache/apt \
    apt-get update && apt-get install -y \
    build-essential \
    libpng-dev \
    libjpeg62-turbo-dev \
    libfreetype6-dev \
	libzip-dev \
    locales \
    zip \
    jpegoptim optipng pngquant gifsicle \
    vim \
    unzip \
    git \
    curl &&\
	curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer &&\
	curl -sL https://deb.nodesource.com/setup_18.x | bash - &&\
    apt update && apt-get install -y nodejs &&\
	apt-get clean && rm -rf /var/lib/apt/lists/*

# Install extensions
RUN docker-php-ext-install mysqli pdo_mysql zip exif pcntl
RUN docker-php-ext-configure gd --with-freetype=/usr/include/ --with-jpeg=/usr/include/
RUN	docker-php-ext-install gd

# check whether group www exists then if not exists add group www with gid 1000 && 
# check whether user www exists then if not exists add user www with uid 1000 to initial group by its gid (1000)
RUN grep -q "^www:" /etc/group || /usr/sbin/groupadd -g 1000 www &&\
    grep -q "^www:" /etc/passwd || /usr/sbin/useradd -u 1000 -ms /bin/bash -g 1000 www
	
# Copy existing application directory contents
COPY . /var/www/example.com

# Copy existing application directory permissions
COPY --chown=www:www . /var/www/example.com

# Change current user to www
USER www

# Expose port 9000 and start php-fpm server
EXPOSE 9000
CMD ["php-fpm"]