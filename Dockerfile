# Dockerfile para aplicación Android
FROM openjdk:17-jdk-slim

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    dos2unix \
    && rm -rf /var/lib/apt/lists/*

# Instalar Android SDK
ENV ANDROID_SDK_ROOT /opt/android-sdk
ENV ANDROID_HOME $ANDROID_SDK_ROOT
ENV PATH $PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools

# Crear directorio para Android SDK
RUN mkdir -p $ANDROID_SDK_ROOT/cmdline-tools

# Descargar Android SDK Command Line Tools
RUN wget -O /tmp/commandlinetools.zip https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip /tmp/commandlinetools.zip -d $ANDROID_SDK_ROOT/cmdline-tools && \
    mv $ANDROID_SDK_ROOT/cmdline-tools/cmdline-tools $ANDROID_SDK_ROOT/cmdline-tools/latest && \
    rm /tmp/commandlinetools.zip

# Aceptar licencias y descargar herramientas necesarias
RUN yes | sdkmanager --licenses
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos del proyecto
COPY . .

# Crear script de inicio que mantiene el contenedor corriendo
RUN echo '#!/bin/bash' > /start.sh && \
    echo 'echo "Arreglando line endings..."' >> /start.sh && \
    echo 'dos2unix ./gradlew 2>/dev/null || true' >> /start.sh && \
    echo 'dos2unix ./gradlew.bat 2>/dev/null || true' >> /start.sh && \
    echo 'find ./gradle/wrapper/ -type f -exec dos2unix {} \; 2>/dev/null || true' >> /start.sh && \
    echo 'chmod +x ./gradlew' >> /start.sh && \
    echo 'echo "Ejecutando gradlew..."' >> /start.sh && \
    echo './gradlew assembleDebug' >> /start.sh && \
    echo 'echo "APK compilada exitosamente!"' >> /start.sh && \
    echo 'echo "Contenedor frontend manteniéndose activo..."' >> /start.sh && \
    echo 'tail -f /dev/null' >> /start.sh && \
    chmod +x /start.sh

# Configurar la URL del backend
ENV BACKEND_URL=http://192.168.100.6:8080

# Exponer puerto para ADB debugging
EXPOSE 5037

# Comando para ejecutar el script de inicio
CMD ["/start.sh"]