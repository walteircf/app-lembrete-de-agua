# Lembrete de Água - App Android 💧

Bem-vindo ao repositório do nosso aplicativo **Lembrete de Água**! Este é um projeto Android nativo, desenvolvido em Java, com o objetivo de ajudar os usuários a manterem-se hidratados de forma personalizada e interativa.

Este documento serve como um guia completo sobre o projeto, desde suas funcionalidades até o passo a passo para compilá-lo.


---

## ✨ Funcionalidades

O aplicativo foi construído com as seguintes funcionalidades em mente:

* **Meta de Hidratação Personalizada:** Ao iniciar o app pela primeira vez, o usuário insere sua idade e peso para que o aplicativo calcule uma meta diária de ingestão de água recomendada.
* **Garrafa Interativa:** Uma representação visual de uma garrafa que se esvazia conforme o usuário registra o consumo de água, proporcionando um feedback visual imediato.
* **Contador de Copos:** Um contador principal que mostra o progresso em relação à meta diária.
* **Múltiplas Formas de Registro:** O usuário pode registrar o consumo "copo a copo" ou registrar que bebeu a garrafa inteira de uma vez.
* **Lembretes Automáticos:** O app envia notificações a cada hora para lembrar o usuário de se hidratar.
* **Lembretes Personalizados:** O usuário pode definir um alarme para um horário específico para um lembrete único.
* **Interface Limpa e Intuitiva:** Um design com fundo branco e botões azuis, focado na usabilidade e clareza.

---

## 🛠️ Tecnologias Utilizadas

Este projeto foi construído utilizando a seguinte pilha de tecnologias:

* **Linguagem:** Java
* **Plataforma:** Android Nativo (SDK)
* **IDE:** IntelliJ IDEA (para a lógica Java) e Visual Studio Code (para os layouts XML)
* **Sistema de Build:** Gradle
* **Interface do Usuário (UI):** XML para layouts e componentes visuais.
* **Armazenamento Local:** `SharedPreferences` para salvar a meta do usuário.
* **Notificações:** `AlarmManager` para agendamento e `NotificationManager` para exibição.

---

## 📁 Estrutura do Projeto

Os arquivos mais importantes do projeto estão organizados da seguinte forma:

```
LembreteDeAgua/
└── app/
    └── src/
        └── main/
            ├── java/com/example/lembretedeagua/
            │   ├── MainActivity.java      # Lógica da tela principal
            │   └── SetupActivity.java       # Lógica da tela de configuração inicial
            │
            ├── res/
            │   ├── drawable/              # Ícones, formas e imagens
            │   │   ├── ic_bottle_outline.xml
            │   │   └── custom_button.xml
            │   │
            │   ├── layout/                # Arquivos de layout (as telas)
            │   │   ├── activity_main.xml
            │   │   └── activity_setup.xml
            │   │
            │   ├── values/                # Definições de cores, strings e temas
            │   │   ├── colors.xml
            │   │   └── themes.xml
            │   │
            │   └── mipmap-hdpi/           # Ícones do aplicativo em diferentes resoluções
            │       └── ic_launcher.webp
            │
            └── AndroidManifest.xml        # O "RG" do aplicativo, define permissões e componentes
```

---

## 🚀 Como Compilar (Gerar o APK)

Para compilar este projeto e gerar um arquivo `.apk` instalável, siga os passos abaixo usando o IntelliJ IDEA:

1.  **Clone ou baixe o projeto.**
2.  **Abra o projeto no IntelliJ IDEA.** Aguarde o Gradle sincronizar todas as dependências.
3.  **Configure o JDK do Gradle:** Vá em `File > Settings > Build, Execution, Deployment > Build Tools > Gradle` e certifique-se de que o **Gradle JDK** está configurado para um JDK completo (como o OpenJDK 17), e não para o JRE embutido do IntelliJ.
4.  **Conecte um dispositivo** ou inicie um emulador.
5.  Para gerar uma versão de teste, vá em **Build > Build Bundle(s) / APK(s) > Build APK(s)**. O arquivo `app-debug.apk` será gerado na pasta `app/build/outputs/apk/debug/`.
6.  Para gerar uma versão final assinada, vá em **Build > Generate Signed Bundle / APK...** e siga o assistente para criar ou usar uma chave de assinatura.

---

## 📖 A Jornada do Desenvolvimento

Este projeto teve uma evolução interessante, passando por várias fases de refinamento, principalmente na interface do usuário.

1.  **A Ideia Inicial:** Começamos com um protótipo web simples, que evoluiu para a ideia de um aplicativo Android nativo para uma experiência mais robusta.

2.  **A Primeira Versão Android:** Criamos a estrutura básica com Java e XML, implementando a lógica de cálculo de metas e o contador.

3.  **A Saga do Roxo:** O primeiro grande desafio foi o tema. O Android Studio/IntelliJ aplicou um tema padrão roxo (Material You) que era muito teimoso. Tentamos várias abordagens, desde aplicar cores diretamente nos layouts até, finalmente, corrigir o problema na raiz, editando os arquivos `themes.xml` para forçar nosso esquema de cores azul e branco.

4.  **O Vazamento da Garrafa:** Outro desafio visual foram as "barras" azuis que vazavam para fora do contorno da garrafa. Isso foi resolvido com um ajuste fino nas dimensões do `FrameLayout` e da `ProgressBar` no arquivo `activity_main.xml`, garantindo que a barra de progresso ficasse perfeitamente contida dentro do espaço da imagem da garrafa.

5.  **Refinamentos Finais:** Por fim, com base no feedback visual, ajustamos os últimos detalhes:

    * Tornamos as linhas e textos dos campos de input na tela de configuração visíveis.
    * Trocamos a cor do contador dentro da garrafa para branco para melhorar o contraste.
    * Demos um estilo mais apropriado ao botão "Reiniciar".

Essa jornada iterativa de build, teste e correção foi crucial para chegar ao resultado final polido.

---

Agradeço pela colaboração e pela oportunidade de construir este projeto!
