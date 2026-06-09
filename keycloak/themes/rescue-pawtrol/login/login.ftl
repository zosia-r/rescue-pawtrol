<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Rescue Pawtrol - Autoryzacja</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${url.resourcesPath}/css/styles.css">
</head>
<body>
  <div class="login-wrapper">
    <div class="login-card">

      <div class="brand-header">
        <div class="logo-box">
          <svg viewBox="0 0 24 24" fill="currentColor" class="heart-icon">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
        </div>
        <div class="brand-text">
          <h1>Rescue Pawtrol</h1>
          <p>Authentication</p>
        </div>
      </div>

      <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post" class="login-form">
        <div class="form-group">
          <label for="username">Login</label>
          <input type="text" id="username" name="username" value="${(login.username!'')}" required placeholder="Wpisz swój login">
        </div>

        <div class="form-group">
          <label for="password">Hasło</label>
          <input type="password" id="password" name="password" required placeholder="Wpisz hasło">
        </div>

        <button type="submit" name="login" id="kc-login" class="primary-btn">Zaloguj się</button>

        <#if message?has_content && (message.type = 'error')>
          <p class="error-msg">${kcSanitize(message.summary)?no_esc}</p>
        </#if>
      </form>

    </div>
  </div>
</body>
</html>