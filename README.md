# VinylCollection - Projeto Final Android Avançado

Este projeto é a entrega final do módulo avançado da trilha de Desenvolvimento Android - 
Capacita Brasil Residência em TIC 20.
Aplicativo para rastrear, pesquisar e organizar coleções de discos de vinil físicos.

Data de entrega:

## Sobre o projeto

O **Vinyl Collection** ajuda colecionadores a manter o acervo organizado. Dá para buscar discos 
direto na [Discogs API](https://www.discogs.com/developers) ou simplesmente escanear o código de 
barras da contracapa com a câmera do celular. Os discos salvos ficam disponíveis offline, para 
consulta rápida e controle de condição do item.

## Funcionalidades

- **Coleção offline-first** — navegue pelos discos salvos mesmo sem internet
- **Busca global** — encontre qualquer lançamento catalogado na Discogs
- **Leitor de código de barras (Smart Scan)** — adicione discos escaneando a contracapa via câmera,
com machine learning integrado
- **Notificações locais** — confirmação ao salvar um novo item no acervo
- **Tema dinâmico** — modo escuro com persistência local

## Tecnologias e Arquitetura

O app foi desenvolvido inteiramente em **Kotlin**, seguindo **MVVM** com padrão **Repository** 
(single source of truth), usando as bibliotecas mais atuais do ecossistema Jetpack:

- **Interface gráfica:** Jetpack Compose (Material Design 3)
- **Navegação:** Navigation Compose
- **Injeção de dependência:** Dagger Hilt
- **Persistência de dados:** Room Database + Flow (Coroutines)
- **Preferências:** DataStore
- **Consumo de API:** Retrofit 2 + OkHttp3 + Gson
- **Carregamento de imagens:** Coil Compose
- **Recursos nativos:** CameraX + Google ML Kit (leitura de código de barras) + Notifications API

## Como rodar o projeto

Para rodar localmente você vai precisar de um token pessoal da API da Discogs.

1. Clone o repositório:
   ```bash
   git clone https://github.com/SEU_USUARIO/VinylCollection.git
   ```
2. Crie uma conta gratuita no [Discogs](https://www.discogs.com/) e gere um token de desenvolvedor.
3. No Android Studio, abra o arquivo `local.properties` (na raiz do projeto) e adicione o seu token:
   ```properties
   DISCOGS_TOKEN="SEU_TOKEN_GERADO_AQUI"
   ```
4. Aguarde o Gradle sincronizar as dependências automaticamente.
5. Conecte um dispositivo Android físico via cabo USB/Wi-Fi
   (com a depuração USB ativada) ou inicie um Emulador no próprio Android Studio.
6. Clique no botão verde de **Run (Play)** na barra superior ou pressione `Shift + F10`.

## Capturas de tela

---

## 👨‍💻 Autor

**Renan Lucas de Moura**
* Estudante de Engenharia da Computação
* [Github](www.github.com/RenanLM)
* [Linkedin](www.linkedin.com/in/renanlmoura)