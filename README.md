# Zguby Gov.pl – System Biura Rzeczy Znalezionych (AI Powered)

Aplikacja typu Full-Stack łącząca **Java Spring Boot** (Backend) oraz **React/Vite** (Frontend). System wykorzystuje sztuczną inteligencję (OpenAI) do automatycznej analizy zdjęć znalezionych przedmiotów oraz do obsługi inteligentnego chatbota dla obywateli.

---

## 📋 Wymagania Wstępne

Aby uruchomić system, upewnij się, że na komputerze zainstalowane są:
1.  **Java JDK 21** (niezbędna dla Backendu).
2.  **Node.js** (wersja 18 lub nowsza) oraz **npm** (niezbędne dla Frontendu).
3.  **Klucz API OpenAI** (zaczynający się od `sk-...`) – wymagany do działania funkcji AI.

---

## 🚀 Instrukcja Uruchomienia

System składa się z dwóch aplikacji, które muszą działać jednocześnie. Należy uruchomić je w dwóch osobnych oknach terminala.

### CZĘŚĆ 1: BACKEND (Spring Boot)

Backend odpowiada za logikę biznesową, komunikację z AI i bazę danych. Działa na porcie **8080**.

1.  **Otwórz terminal** i wejdź do katalogu backendu:
    ```bash
    cd backend
    ```

2.  **Skonfiguruj klucz API:**
    Otwórz plik `src/main/resources/application.properties`. Jeśli nie istnieje, utwórz go i wklej poniższą zawartość (uzupełniając swój klucz):

    ```properties
    spring.application.name=backend
    
    # --- KONFIGURACJA OPENAI (WYMAGANA) ---
    # Wklej tutaj swój klucz API OpenAI
    spring.ai.openai.api-key=sk-TU_WPISZ_SWOJ_KLUCZ_OPENAI
    spring.ai.openai.chat.options.model=gpt-4o-mini
    
    # --- BAZA DANYCH (H2 In-Memory) ---
    spring.datasource.url=jdbc:h2:mem:testdb
    spring.datasource.driverClassName=org.h2.Driver
    spring.datasource.username=sa
    spring.datasource.password=password
    spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
    spring.h2.console.enabled=true
    
    # --- USTAWIENIA JSON ---
    spring.jackson.deserialization.fail-on-unknown-properties=false
    spring.jackson.mapper.accept-case-insensitive-enums=true
    ```

3.  **Uruchom serwer:**
    Wpisz w terminalu odpowiednią komendę:
    * **Windows:**
        ```powershell
        gradlew.bat bootRun
        ```
    * **Linux / macOS:**
        ```bash
        ./gradlew bootRun
        ```

    > **Sukces:** Backend jest gotowy, gdy zobaczysz w logach komunikat: `Started BackendApplication in ... seconds`.

---

### CZĘŚĆ 2: FRONTEND (React + Vite)

Frontend to interfejs użytkownika dla urzędnika i obywatela. Działa na porcie **5173**.

1.  **Otwórz NOWY terminal** i wejdź do katalogu frontendu:
    ```bash
    cd zguby-gov-frontend
    ```

2.  **Skonfiguruj połączenie:**
    Utwórz plik `.env` w głównym folderze frontendu (obok `package.json`) i wklej:

    ```env
    VITE_API_URL=http://localhost:8080
    VITE_USE_MOCKS=false
    ```

3.  **Zainstaluj biblioteki:**
    ```bash
    npm install
    ```

4.  **Uruchom aplikację:**
    ```bash
    npm run dev
    ```

5.  **Otwórz w przeglądarce:**
    Kliknij link wyświetlony w terminalu: **http://localhost:5173**.

---

## 💡 Scenariusze Testowe (Przewodnik dla Egzaminatora)

### Scenariusz A: Panel Urzędnika – Dodawanie rzeczy z AI
Testuje: *Analizę obrazu, zapis do bazy danych, listowanie elementów.*

1.  W aplikacji kliknij przycisk **"Panel urzędnika"** (prawy górny róg) lub wybierz z menu.
2.  Wybierz zakładkę **"Dodaj rzecz"**.
3.  Przeciągnij zdjęcie przedmiotu (np. klucze, telefon, portfel) na pole "Wgraj zdjęcie".
4.  Kliknij przycisk **"Analizuj zdjęcia"**.
    * *System wyśle zdjęcie do OpenAI i automatycznie wypełni formularz (Tytuł, Kategoria, Kolor, Opis).*
5.  Przejdź przez kolejne kroki (Edycja -> Lokalizacja -> Potwierdzenie).
6.  Kliknij **"Zapisz do bazy"**. Zobaczysz komunikat sukcesu.
7.  Przejdź do zakładki **"Lista rzeczy"** – nowo dodany przedmiot będzie widoczny na liście.

### Scenariusz B: Chatbot dla Obywatela – Zgłaszanie zguby
Testuje: *Konwersację AI, pamięć kontekstową, generowanie ticketów.*

1.  Wróć na stronę główną (kliknij logo `gov.pl`).
2.  Wpisz wiadomość do asystenta, np.: *"Dzień dobry, zgubiłem plecak w autobusie"*.
3.  Chatbot zada pytania doprecyzowujące (o kolor, markę, datę). Odpowiadaj na nie.
4.  Po około 5-6 wymianach zdań chatbot automatycznie:
    * Zakończy wywiad.
    * Wygeneruje **Zgłoszenie (Ticket)**.
    * Wyświetli **Numer Zgłoszenia** oraz **Kod QR**.
5.  (Opcjonalnie) Jako urzędnik wejdź w **Panel urzędnika** -> **Tickety**, aby zobaczyć to zgłoszenie i zmienić jego status.

---

## ⚠️ Rozwiązywanie Problemów

| Problem | Możliwa przyczyna | Rozwiązanie |
| :--- | :--- | :--- |
| **Błąd 500 (przy analizie zdjęcia)** | Problem z kluczem OpenAI (brak środków, wygasły klucz, literówka). | Sprawdź plik `application.properties`. System ma zabezpieczenie (fallback) – pozwoli zapisać przedmiot ręcznie nawet przy błędzie AI. |
| **Network Error / CORS** | Backend nie działa lub port 8080 jest zajęty. | Upewnij się, że backend jest uruchomiony. Jeśli masz błąd portu, zamknij procesy Javy: `pkill -f java` (Linux/Mac) lub `taskkill /F /IM java.exe` (Windows) i uruchom ponownie. |
| **Brak danych po restarcie** | Baza danych H2 działa w pamięci RAM. | To normalne zachowanie. Każdy restart backendu czyści bazę danych. |
| **Błąd 400 (Bad Request)** | Stara wersja frontendu wysyła zły format daty. | Upewnij się, że masz najnowszy kod frontendu, który formatuje daty do `YYYY-MM-DD`. |