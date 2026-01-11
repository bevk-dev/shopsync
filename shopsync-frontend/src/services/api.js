//const BASE_URL = 'http://10.0.2.2:8080/api'; // 10.0.2.2 je localhost za Android emulator
export const BASE_URL = 'http://192.168.1.16:8080/api';

export const syncUser = async (token) => {
  try {
    const response = await fetch(`${BASE_URL}/users/me`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) {
        console.log("Status napake:", response.status);
        throw new Error('Napaka pri sinhronizaciji uporabnika');
    }
    return await response.json();
  } catch (error) {
    console.error("Sync Error:", error);
    throw error;
  }
};

export const fetchUserLists = async (token) => {
  try {
    const response = await fetch(`${BASE_URL}/shopping-lists`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) throw new Error('Napaka pri pridobivanju seznamov');
    return await response.json();
  } catch (error) {
    console.error("API Error:", error);
    throw error;
  }
};