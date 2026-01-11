import React, { useState, useEffect } from 'react';
import { 
  StyleSheet, Text, View, Button, ActivityIndicator, 
  FlatList, TextInput, Alert, TouchableOpacity 
} from 'react-native';
import { useAuth0 } from 'react-native-auth0';
import { BASE_URL } from '../services/api';

const HomeScreen = ({ navigation, route }) => {
  const { user, clearSession } = useAuth0();
  const accessToken = route.params?.token; // Žeton dobimo iz LoginScreena

  const [lists, setLists] = useState([]);
  const [isFetching, setIsFetching] = useState(false);
  const [newListName, setNewListName] = useState('');

  const fetchLists = async () => {
    if (!accessToken) return;
    setIsFetching(true);
    try {
      const response = await fetch(`${BASE_URL}/shopping-lists`, {
        headers: { 'Authorization': `Bearer ${accessToken}` }
      });
      if (response.ok) {
        const data = await response.json();
        setLists(data);
      }
    } catch (err) {
      console.log("Fetch lists error:", err);
    } finally {
      setIsFetching(false);
    }
  };

  const handleCreateList = async () => {
    if (!newListName.trim()) return;
    try {
      const response = await fetch(`${BASE_URL}/shopping-lists`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${accessToken}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name: newListName })
      });
      if (response.ok) {
        setNewListName('');
        fetchLists();
      }
    } catch (err) {
      Alert.alert("Napaka", "Povezava do strežnika ni uspela.");
    }
  };

  useEffect(() => {
    fetchLists();
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.welcome}>Pozdravljen, {user?.name}!</Text>
      
      <View style={styles.inputContainer}>
        <TextInput 
          style={styles.input}
          placeholder="Ime novega seznama..."
          value={newListName}
          onChangeText={setNewListName}
        />
        <Button title="Dodaj" onPress={handleCreateList} />
      </View>

      <FlatList
        data={lists}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <TouchableOpacity 
            style={styles.listItem}
            onPress={() => navigation.navigate('Items', { 
              listId: item.id, 
              listName: item.name,
              token: accessToken 
            })}
          >
            <Text style={styles.itemText}>{item.name}</Text>
          </TouchableOpacity>
        )}
        refreshing={isFetching}
        onRefresh={fetchLists}
      />
      
      <Button title="Odjava" onPress={() => { clearSession(); navigation.replace('Login'); }} color="red" />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#f5f5f5' },
  welcome: { fontSize: 18, marginBottom: 20, fontWeight: 'bold' },
  inputContainer: { flexDirection: 'row', marginBottom: 20 },
  input: { flex: 1, backgroundColor: '#fff', padding: 10, borderRadius: 5, marginRight: 10, borderWidth: 1, borderColor: '#ddd' },
  listItem: { backgroundColor: '#fff', padding: 15, borderRadius: 10, marginBottom: 10, elevation: 2 },
  itemText: { fontSize: 18 }
});

export default HomeScreen;