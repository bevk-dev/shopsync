import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Auth0Provider } from 'react-native-auth0';
import { SafeAreaProvider } from 'react-native-safe-area-context';

// Uvozi zaslone (te datoteke bova ustvarila v naslednjem koraku)
import HomeScreen from './src/screens/HomeScreen';
import ItemsScreen from './src/screens/ItemsScreen';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <SafeAreaProvider>
      {/* Auth0Provider mora biti okoli vsega, da imajo vsi zasloni dostop do prijave */}
      <Auth0Provider 
        domain="shopsync.eu.auth0.com" 
        clientId="XkfZw7RZVYOV83ZTN6kgohunzZOrjV8M"
      >
        <NavigationContainer>
          <Stack.Navigator initialRouteName="Home">
            
            {/* Glavni zaslon s seznami */}
            <Stack.Screen 
              name="Home" 
              component={HomeScreen} 
              options={{ title: 'ShopSync 🛒' }} 
            />

            {/* Zaslon z artikli specifičnega seznama */}
            <Stack.Screen 
              name="Items" 
              component={ItemsScreen} 
              options={({ route }) => ({ title: route.params.listName })} 
            />

          </Stack.Navigator>
        </NavigationContainer>
      </Auth0Provider>
    </SafeAreaProvider>
  );
}