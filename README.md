# VinylCollection — Gerenciador de Coleção de Discos de Vinil

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Repository-orange.svg)](https://developer.android.com/topic/architecture)
[![Room](https://img.shields.io/badge/Database-Room-lightgrey.svg)](https://developer.android.com/training/data-storage/room)
[![Hilt](https://img.shields.io/badge/DI-Dagger%20Hilt-red.svg)](https://dagger.dev/hilt/)

VinylCollection é um app Android nativo para catalogar e organizar discos de vinil. Ele integra com 
a API do [Discogs](https://www.discogs.com/developers) para buscar informações de álbuns, permite 
escanear o código de barras da contracapa pela câmera, e funciona offline usando um banco local que 
sincroniza com a conta do usuário no Discogs.

Projeto de conclusão do Módulo Avançado da Trilha em Desenvolvimento Android (Residência em TIC 20 - Capacita Brasil).

Data de entrega: 31/08/2026

---

| Demonstração Principal | Notificação e Deep Link |
| :---: | :---: |
| <img src="docs/MainGif.gif" width="250" alt="Demonstração Principal" /> | <img src="docs/NotifyGif.gif" width="250" alt="Notificação e Deep Link" /> |

## Arquitetura

O app segue MVVM combinado com Repository Pattern, onde o repositório funciona como fonte única da 
verdade entre o banco local e a API remota.

```
                      ┌────────────────────────┐
                      │    Jetpack Compose     │
                      │  (UI / Screen Views)   │
                      └───────────┬────────────┘
                                  │ (State / Events)
                                  ▼
                      ┌────────────────────────┐
                      │  ViewModels & State    │
                      │ (Vinyl / Settings VM)  │
                      └───────────┬────────────┘
                                  │
                                  ▼
                      ┌────────────────────────┐
                      │    VinylRepository     │
                      └─────┬────────────┬─────┘
                            │            │
            ┌───────────────┘            └───────────────┐
            ▼                                            ▼
┌───────────────────────┐                    ┌───────────────────────┐
│     Room Database     │                    │    Retrofit Client    │
│  (Offline Collection) │                    │    (Discogs API)      │
└───────────────────────┘                    └───────────────────────┘
```

### Stack

- **Kotlin** — Coroutines, Flow, StateFlow, combine.
- **Jetpack Compose** com Material Design 3 (Scaffold, LazyColumn, TopAppBar, FAB, temas dinâmicos).
- **Dagger Hilt** para injeção de dependência em telas, repositórios e workers.
- **Navigation Compose**, com transições customizadas entre telas e suporte a **Deep Linking** 
(`vinylcollection://detail/{vinylId}`) para navegação direta via notificações.
- **Room** para persistência local, com relacionamentos entre entidades e queries reativas via Flow.
- **DataStore Preferences** para salvar tema (modo escuro) e critério de ordenação.
- **Retrofit 2 + OkHttp3 + Gson** para consumo da API do Discogs, com interceptador de log e header 
de autenticação.
- **Coil** para carregamento assíncrono de imagens (capas dos álbuns).
- **CameraX + ML Kit Barcode Scanning** para leitura de código de barras em tempo real.
- **WorkManager + Hilt Worker** para a tarefa em segundo plano que sorteia o "Disco do Dia".

---

## Funcionalidades

O app tem 4 telas principais:

**Minha Coleção (Home)**
Lista os discos salvos localmente, com ordenação configurável (Recentes, Título A-Z, Artista A-Z) e 
um card por disco mostrando capa, título, artista e condição. Um FAB leva para a busca.

**Buscar na Discogs**
Busca por texto na base do Discogs. Tem um botão de câmera integrado ao campo de busca que abre o 
leitor de código de barras (Smart Scan) — aponta pra contracapa do vinil e ele já preenche a busca. 
A tela trata os estados de idle, loading, sucesso e erro.

**Detalhes do Disco**
Mostra capa em alta resolução, título, artista, ano, gênero e condição. Dá pra salvar o disco na 
coleção (grava no Room e faz POST pro Discogs) ou remover (deleta do Room e faz DELETE remoto). 
Também é aqui que ficam as tarefas de manutenção do disco (ex: "limpar vinil", "trocar plástico 
protetor") — cada disco pode ter várias, numa relação 1:N.

**Configurações**
Modo escuro e critério de ordenação padrão, salvos via DataStore.

---

## Banco de dados local (Room)

Duas entidades:

- **VinylRecord** (`vinyl_records`) — `id`, `discogsId`, `instanceId`, `title`, `artist`, 
`coverUrl`, `barcode`, `condition`, `year`, `genre`.
- **Task** (`tasks`) — `id`, `vinylRecordId` (FK para `VinylRecord.id`, `ON DELETE CASCADE`),
`description`, `isCompleted`.

Operações implementadas: `insertVinylRecord()`, `insertTask()`, `getAllVinylRecords()`, 
`getVinylRecordById()`, `getTasksByVinylRecordId()`, `getRandomVinylRecord()` (usado pelo worker 
do "Disco do Dia"), `updateVinylRecord()`, `updateTask()`, `deleteVinylRecord()`, `deleteTask()`.

---

## Endpoints da Discogs consumidos

Via `DiscogsApiService`:

- `GET oauth/identity` — identifica o usuário autenticado.
- `GET database/search` — busca por texto ou código de barras.
- `GET users/{username}/collection/releases/{release_id}` — instâncias de um álbum na coleção remota.
- `POST users/{username}/collection/folders/1/releases/{release_id}` — adiciona lançamento à coleção.
- `PUT .../instances/{instance_id}` — atualiza condição do item.
- `DELETE .../instances/{instance_id}` — remove da coleção remota.

---

## Permissões e notificações

- `INTERNET` — para as chamadas à API e carregamento de capas.
- `CAMERA` — solicitada em runtime, ao abrir o leitor de código de barras.
- `POST_NOTIFICATIONS` — solicitada em runtime no Android 13+.

O `VinylNotificationManager` cria o canal `vinyl_collection_channel` e dispara duas notificações: 
confirmação ao adicionar um vinil à coleção, e a recomendação diária do "Disco do Dia". Ao clicar 
na notificação do disco do dia, o aplicativo abre diretamente na tela de detalhes do vinil sorteado 
via Deep Link (`vinylcollection://detail/$vinylId`).

---

## Recursos extras

- **Leitor de código de barras** — CameraX processa os frames da câmera em tempo real e o ML Kit 
decodifica o código, preenchendo a busca automaticamente.
- **DailyVinylWorker e Deep Linking** — agendado no início da aplicação via 
`WorkManager.enqueueUniquePeriodicWork()`, roda a cada 24h, sorteia um vinil da coleção e notifica 
o usuário. Ao tocar na notificação, um PendingIntent de Deep Link abre o app diretamente na tela de 
detalhes do vinil sorteado.
- **Transições animadas** no NavHost (slide + fade entre telas).

---

## Como rodar

### Pré-requisitos

- Android Studio Ladybug (2024.2.1) ou superior
- JDK 11 ou 17
- SDK: `compileSdk` 37, `targetSdk` 36, `minSdk` 33 (Android 13+)
- Emulador API 33+ ou dispositivo físico (necessário um dispositivo real para testar o scanner de 
código de barras)

### Passo a passo

1. Clone o repositório:
   ```bash
   git clone https://github.com/SEU_USUARIO/VinylCollection.git
   cd VinylCollection
   ```

2. Configure a chave da API do Discogs:
    - Crie uma conta em [Discogs.com](https://www.discogs.com/) (ou use uma existente)
    - Gere um Personal Access Token em [Configurações de Desenvolvedor](https://www.discogs.com/settings/developers)
    - Na raiz do projeto, crie ou abra o `local.properties` e adicione:
      ```properties
      DISCOGS_TOKEN="SEU_TOKEN_AQUI"
      ```

3. Abra o projeto no Android Studio e aguarde o Gradle Sync.

4. Selecione a configuração `app`, escolha o dispositivo e rode (`Shift + F10`).

---

## Autor

**Renan Lucas de Moura**  
Estudante de Engenharia da Computação  
GitHub: [github.com/RenanLM](https://github.com/RenanLM)  
LinkedIn: [linkedin.com/in/renanlmoura](https://www.linkedin.com/in/renanlmoura)  