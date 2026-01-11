import React from 'react';
import { View, Button, StyleSheet, Text } from 'react-native';
import { useAuth0 } from 'react-native-auth0';
import { syncUser } from '../services/api';

const LoginScreen = ({ navigation }) => {
  const { authorize, getCredentials } = useAuth0();

  const handleLogin = async () => {
    try {
      // 1. Auth0 Prijava
      await authorize({ 
        audience: 'https://shopsync-api.com', 
        scope: 'openid profile email' 
      });
      
      // 2. Žeton
      const credentials = await getCredentials();
      
      // 3. Sinhronizacija s PostgreSQL bazo
      console.log("Sinhronizacija z bazo...");
      await syncUser(credentials.accessToken);
      
      // 4. Preklop na Home in prenos žetona
      navigation.replace('Home', { token: credentials.accessToken });
    } catch (e) {
      console.error("Login napaka:", e);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>ShopSync 🛒</Text>
      <Button title="Prijavi se" onPress={handleLogin} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#fff' },
  title: { fontSize: 24, fontWeight: 'bold', marginBottom: 20 }
});

export default LoginScreen;