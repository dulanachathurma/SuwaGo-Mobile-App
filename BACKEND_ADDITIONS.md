# Backend additions (Express on Vercel)

The Android app v1.1 calls **two new routes**. Everything else (map, symptom checker, notifications)
works without backend changes.

| Feature          | Route                      | If the route is missing                          |
|------------------|----------------------------|--------------------------------------------------|
| Change password  | `PUT  /api/users/password` | App shows "This feature is not available yet."   |
| AI assistant     | `POST /api/ai/chat`        | App automatically uses a basic offline answer    |

Names below (`auth` middleware, `User` model, `req.user.id`) are examples - rename them to match
your existing backend files.

---

## 1. Change password  -  `PUT /api/users/password`

```js
// routes/users.js  (or wherever your profile routes live)
const bcrypt = require('bcryptjs');          // use the same bcrypt package as your login route

router.put('/password', auth, async (req, res) => {
  try {
    const { currentPassword, newPassword } = req.body || {};
    if (!currentPassword || !newPassword || String(newPassword).length < 6) {
      return res.status(400).json({ code: 'INVALID_INPUT' });
    }

    const user = await User.findById(req.user.id);          // adapt to your auth middleware
    if (!user) return res.status(401).json({ code: 'AUTH_REQUIRED' });

    const ok = await bcrypt.compare(currentPassword, user.password);
    if (!ok) return res.status(400).json({ code: 'WRONG_PASSWORD' });

    // If your User model already hashes in a pre('save') hook, assign the plain password instead.
    user.password = await bcrypt.hash(newPassword, 10);
    await user.save();

    res.json({ ok: true });
  } catch (e) {
    res.status(500).json({ code: 'SERVER_ERROR' });
  }
});
```
Mount it so the final URL is `/api/users/password` (same place as `/api/users/profile`).

---

## 2. AI assistant  -  `POST /api/ai/chat`

The Anthropic API key must stay **on the server**. Never put it inside the Android app.

1. In Vercel: **Project -> Settings -> Environment Variables** add `ANTHROPIC_API_KEY`
   (optional: `ANTHROPIC_MODEL`, default below).
2. Add the route (Node 18+ has `fetch` built in):

```js
// routes/ai.js
const express = require('express');
const router = express.Router();

const MODEL = process.env.ANTHROPIC_MODEL || 'claude-haiku-5-5';
const LANG = { en: 'English', si: 'Sinhala', ta: 'Tamil' };

router.post('/chat', auth, async (req, res) => {
  try {
    const { messages, language, profile } = req.body || {};
    if (!Array.isArray(messages) || messages.length === 0) {
      return res.status(400).json({ code: 'INVALID_INPUT' });
    }

    // keep the last 12 turns, plain strings only, first message must be from the user
    let history = messages.slice(-12)
      .filter(m => (m.role === 'user' || m.role === 'assistant') && typeof m.content === 'string')
      .map(m => ({ role: m.role, content: m.content.slice(0, 2000) }));
    while (history.length && history[0].role !== 'user') history.shift();
    if (!history.length) return res.status(400).json({ code: 'INVALID_INPUT' });

    const lang = LANG[language] || 'English';
    const p = profile
      ? ` The user's basic data: age ${profile.age ?? 'unknown'}, height ${profile.heightCm ?? 'unknown'} cm, ` +
        `weight ${profile.weightKg ?? 'unknown'} kg, BMI ${profile.bmi ?? 'unknown'}.`
      : '';

    const system =
      'You are the VitaCare AI health assistant inside a mobile app. ' +
      `Always answer in ${lang}. Be warm, clear and short (max about 120 words). ` +
      'Plain text only: no markdown, no asterisks, no headings. ' +
      'Give general health education, never a diagnosis or prescription doses. ' +
      'You may suggest which type of doctor to visit. ' +
      'If the message mentions chest pain, trouble breathing, stroke signs, heavy bleeding, ' +
      'suicidal thoughts or another emergency, tell the user to go to the nearest hospital or call emergency services now.' +
      p;

    const r = await fetch('https://api.anthropic.com/v1/messages', {
      method: 'POST',
      headers: {
        'content-type': 'application/json',
        'x-api-key': process.env.ANTHROPIC_API_KEY,
        'anthropic-version': '2023-06-01',
      },
      body: JSON.stringify({ model: MODEL, max_tokens: 500, system, messages: history }),
    });

    if (!r.ok) return res.status(502).json({ code: 'SERVER_ERROR' });
    const data = await r.json();
    const reply = (data.content || []).filter(b => b.type === 'text').map(b => b.text).join('\n').trim();
    if (!reply) return res.status(502).json({ code: 'SERVER_ERROR' });

    res.json({ reply });
  } catch (e) {
    res.status(500).json({ code: 'SERVER_ERROR' });
  }
});

module.exports = router;
```
3. Mount it: `app.use('/api/ai', aiRoutes);` and redeploy on Vercel.

Only age, height, weight and BMI are sent to the AI. Name, email and photo are never sent.
