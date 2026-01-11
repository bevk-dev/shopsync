const BASE_URL = 'http://10.0.2.2:8080/api'; // 10.0.2.2 je localhost za Android emulator

export const fetchUserLists = async (token) => {
  try {
    const response = await fetch(`${BASE_URL}/lists`, { // prilagodi endpoint svojemu backendu
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