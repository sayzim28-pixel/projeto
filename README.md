# Texture Overlay - Android App

App Android com botao flutuante (overlay) que copia arquivos para diretorios especificos.

## Estrutura do Projeto

```
TextureOverlay/
├── app/
│   ├── src/main/
│   │   ├── java/com/rato/textureoverlay/
│   │   │   ├── MainActivity.kt          # Activity principal
│   │   │   └── OverlayService.kt       # Servico de overlay
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml   # Layout do app
│   │   │   │   └── floating_button.xml # Layout do botao flutuante
│   │   │   ├── drawable/
│   │   │   │   ├── floating_button_bg.xml
│   │   │   │   └── ic_launcher.xml
│   │   │   └── values/
│   │   │       ├── strings.xml
│   │   │       └── themes.xml
│   │   ├── assets/
│   │   │   ├── fileinfo                 # Arquivo de info
│   │   │   └── shaders.F3kBzwdDqkGcpDWPbhf2lNZWvXA~3D  # Shader
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradle/wrapper/
    └── gradle-wrapper.properties
```

## Funcionalidades

- Botao flutuante que aparece sobre outros aplicativos
- Arraste para reposicionar o botao
- Toque para copiar arquivos:
  - `fileinfo` → `/storage/emulated/0/Android/data/com.dts.freefireth/files/contentcache/Optional/android/fileinfo`
  - `shaders.*` → `/storage/emulated/0/Android/data/com.dts.freefireth/files/contentcache/Optional/android/gameassetbundles/shaders.F3kBzwdDqkGcpDWPbhf2lNZWvXA~3D`

## Requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK API 34
- Dispositivo Android API 23+ (Android 6.0+)

## Como Compilar

### Opcao 1: Android Studio

1. Abra o Android Studio
2. Selecione "Open an Existing Project"
3. Navegue ate a pasta `TextureOverlay` e clique "OK"
4. Aguarde o Gradle sincronizar
5. Conecte seu dispositivo Android ou inicie um emulador
6. Clique em Run (Shift+F10) ou Build > Make Project (Ctrl+F9)

### Opcao 2: Linha de Comando

```bash
# Navegue ate a pasta do projeto
cd TextureOverlay

# Compile o APK de debug
./gradlew assembleDebug

# O APK estara em: app/build/outputs/apk/debug/app-debug.apk
```

### Opcao 3: Gerar APK Assinado

```bash
./gradlew assembleRelease
# APK: app/build/outputs/apk/release/app-release.apk
```

### Opcao 4: GitHub Actions (Compilar na Nuvem)

1. Crie um repositorio no GitHub
2. Faça push do projeto:
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/SEU_USUARIO/SEU_REPO.git
   git push -u origin main
   ```
3. O GitHub Actions ira compilar automaticamente
4. Va em "Actions" no repositorio > clique no workflow concluido
5. Baixe os APKs em "Artifacts":
   - `app-debug` - APK de debug
   - `app-release` - APK de release (nao assinado)

**Trigger manual:** Va em Actions > Build Android APK > Run workflow

## Instalacao no Dispositivo

1. Transfira o APK para o dispositivo
2. Instale o APK (pode precisar permitir "Instalar de fontes desconhecidas")
3. Abra o app
4. Conceda permissao de overlay quando solicitado
5. Clique em "Iniciar Overlay"

## Permissoes Necessarias

- `SYSTEM_ALERT_WINDOW` - Para exibir o botao flutuante sobre outros apps
- `FOREGROUND_SERVICE` - Para manter o servico rodando em background

## Uso

1. Abra o app e clique "Iniciar Overlay"
2. Um botao verde circular aparecera na tela
3. Arraste para reposicionar
4. Toque para copiar os arquivos
5. Clique "Parar Overlay" no app principal para remover o botao

## Personalizar Arquivos

Para usar seus proprios arquivos, substitua os arquivos em `app/src/main/assets/`:
- `fileinfo` - seu arquivo de configuracao
- `shaders.F3kBzwdDqkGcpDWPbhf2lNZWvXA~3D` - seu shader

## Solucao de Problemas

### "Permissao negada ao copiar arquivos"
- No Android 11+, apps so podem escrever em seus proprios diretorios
- O app `com.rato.textura` precisa existir no dispositivo
- Para Android 10-, verifique permissoes de armazenamento

### "Botao nao aparece"
- Verifique se a permissao SYSTEM_ALERT_WINDOW foi concedida
- Va em Configuracoes > Apps > Texture Overlay > "Exibir sobre outros apps"

### Build falha
- Verifique se tem JDK 17 instalado
- Sincronize o projeto: File > Sync Project with Gradle Files
- Limpe e reconstrua: Build > Clean Project, depois Build > Rebuild Project
