const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090')
  .replace(/\/+$/, '');

export async function postJson(path, payload, { credentials } = {}) {
  const url = `${API_BASE_URL}${path}`;
  const loggedPayload = { ...payload };
  if ('password' in loggedPayload) {
    loggedPayload.password = '[REDACTED]';
  }

  console.info('API request started', { url, payload: loggedPayload });

  let response;
  try {
    response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      ...(credentials ? { credentials } : {}),
      body: JSON.stringify(payload),
    });
  } catch (error) {
    console.error('API request failed before receiving a response', { url, error });
    throw new Error(`Could not reach the backend at ${url}. Check that it is running and CORS allows this frontend origin.`);
  }

  const responseText = await response.text();
  let data = null;
  if (responseText) {
    try {
      data = JSON.parse(responseText);
    } catch {
      data = responseText;
    }
  }

  console.info('API response received', { url, status: response.status, data });
  if (!response.ok) {
    const message = data && typeof data === 'object'
      ? data.error || data.message
      : null;
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  if (!data || typeof data !== 'object') {
    throw new Error(`Backend returned an invalid response for ${url}`);
  }

  return data;
}
