# 🧵 Projeto de Atividades - Sistemas Operacionais com Java e Threads

Este projeto reúne quatro atividades práticas que exploram conceitos fundamentais de **concorrência**, **sincronização de threads**, **estruturas de dados thread-safe** e **controle de acesso em sistemas simulados** com Java.

---

## 🚀 Tecnologias Utilizadas

- Java 17+
- `java.util.concurrent` (ExecutorService, Semaphore, ReentrantLock)
- JMH (para benchmarking do Exercício 3, opcional)
- Maven (ou Gradle, à escolha do grupo)

---

## 🔀 Organização do Repositório

Cada exercício deve ser implementado em **uma branch separada**, com o seguinte padrão:

| Branch        | Descrição resumida                             |
|---------------|------------------------------------------------|
| `exercicio-1` | Barbeiro Dorminhoco com 2 barbeiros e 10 vagas |
| `exercicio-2` | ArrayList thread-safe                          |
| `exercicio-3` | Benchmark entre ArrayList thread-safe e Vector |
| `exercicio-4` | Controle concorrente de acesso a BD (CRUD)     |

---

## 🧠 Descrição dos Exercícios

### ✅ Exercício 1: Barbeiro Dorminhoco com 2 barbeiros

- Fila com no máximo 10 clientes.
- Clientes chegam a cada **4 a 6 segundos**.
- Corte de cabelo demora entre **5 a 10 segundos**.
- Se a fila estiver cheia, o cliente vai embora com mensagem informativa.
- Dois barbeiros atendem simultaneamente.
- Use `BlockingQueue`, `ExecutorService`, `Random`, `Thread.sleep()`.

---

### ✅ Exercício 2: ArrayList Thread-Safe

- Criar uma implementação de `ArrayList` segura para múltiplas threads.
- **Consultas podem ocorrer simultaneamente**.
- **Inserções e remoções exigem sincronização**.
- Sugestão: usar `ReentrantReadWriteLock` para controlar leitura e escrita.
- Implementar os principais métodos: `add()`, `remove()`, `get()` etc.

---

### ✅ Exercício 3: Benchmark - ArrayList Thread-Safe vs Vector

- Comparar desempenho das estruturas:
    - Inserção
    - Busca
    - Remoção
- Testes com:
    - **1 thread**
    - **16 threads simultâneas**
- Usar `System.nanoTime()` (ou JMH) para medir tempo de execução.
- Cada thread executa uma quantidade definida de operações.
- Ao final, apresentar:
    - Tempo médio por operação
    - Número de operações por segundo
    - Comparação entre ArrayList Thread-Safe e `Vector`

---

### ✅ Exercício 4: Simulação de Banco de Dados com Acesso Concorrente

- Até **10 consultas (read)** simultâneas.
- Apenas **1 operação de escrita (insert, update, delete)** por vez.
- Durante uma **escrita**, nenhuma leitura pode ocorrer.
- Durante leituras, **nenhuma escrita é permitida**.
- Criar uma classe central de controle de acesso (`DatabaseAccessController`).
- Implemente os métodos:
    - `create()`
    - `read()`
    - `update()`
    - `delete()`
- Criar um programa para testar com várias threads simulando usuários.

---

## 📁 Estrutura de Pastas Recomendada

    src/
        ├── main/
    
        │ └── java/org/example/sistemasoperacionais/
            │ ├── exercicio1/
            │ ├── exercicio2/
            │ ├── exercicio3/
            │ └── exercicio4/
    
        └── test/

---

## ⚙️ Comandos Úteis

### 🛠️ Maven

```bash
# Compilar o projeto
./mvnw clean install

# Rodar uma classe específica (exemplo: Exercício 1)
./mvnw exec:java -Dexec.mainClass="org.example.sistemasoperacionais.exercicio1.Main"

```
* Use System.out.println() para mostrar quando uma thread está ativa, esperando ou finalizou.
* Evite deadlocks usando tryLock(), Semaphore, ReentrantLock, ReadWriteLock.
* Use Executors para controle de threads quando aplicável.
* Documente seu código e resultados, especialmente no Exercício 3.
