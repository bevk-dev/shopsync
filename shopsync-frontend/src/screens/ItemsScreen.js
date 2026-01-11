import React, { useState, useEffect } from 'react';
import { 
  View, Text, TextInput, FlatList, StyleSheet, 
  ActivityIndicator, Button, Alert, TouchableOpacity 
} from 'react-native';

const ItemsScreen = ({ route }) => {
  const { listId, token } = route.params;
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Stanja za nov artikel
  const [newItemName, setNewItemName] = useState('');
  const [quantity, setQuantity] = useState('1');

  useEffect(() => {
    fetchItems();
  }, []);

  const fetchItems = async () => {
    try {
      // Pridobivanje artiklov za specifičen seznam
      const response = await fetch(`http://10.0.2.2:8080/api/shopping-lists/${listId}/items`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const data = await response.json();
      setItems(data);
    } catch (error) {
      console.error("Napaka pri branju artiklov:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddItem = async () => {
    if (!newItemName.trim()) return;
    try {
      // Dodajanje novega artikla v bazo (ShoppingListController)
      const response = await fetch(`http://10.0.2.2:8080/api/shopping-lists/${listId}/items`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ 
          name: newItemName, 
          quantity: parseInt(quantity) || 1,
          unit: 'kos' 
        })
      });

      if (response.ok) {
        setNewItemName('');
        setQuantity('1');
        fetchItems();
      }
    } catch (err) {
      Alert.alert("Napaka", "Ni bilo mogoče dodati artikla.");
    }
  };

  const togglePurchased = async (itemId) => {
    try {
      // Preklop stanja kupljeno (ItemController)
      const response = await fetch(`http://10.0.2.2:8080/api/items/${itemId}/purchase`, {
        method: 'PATCH',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) fetchItems();
    } catch (err) {
      console.error("Napaka pri posodabljanju:", err);
    }
  };

  const deleteItem = async (itemId) => {
    try {
      // Brisanje artikla (ItemController)
      const response = await fetch(`http://10.0.2.2:8080/api/items/${itemId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) fetchItems();
    } catch (err) {
      console.error("Napaka pri brisanju:", err);
    }
  };

  if (loading) return <ActivityIndicator style={{ marginTop: 50 }} />;

  return (
    <View style={styles.container}>
      <View style={styles.inputContainer}>
        <TextInput 
          style={[styles.input, { flex: 3 }]} 
          placeholder="Ime artikla..."
          value={newItemName}
          onChangeText={setNewItemName}
        />
        <TextInput 
          style={[styles.input, { flex: 1, textAlign: 'center' }]} 
          placeholder="Št."
          value={quantity}
          onChangeText={setQuantity}
          keyboardType="numeric"
        />
        <Button title="Dodaj" onPress={handleAddItem} />
      </View>

      <FlatList
        data={items}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <View style={[styles.itemRow, item.purchased && styles.purchasedRow]}>
            <TouchableOpacity 
              style={{ flex: 1 }} 
              onPress={() => togglePurchased(item.id)}
            >
              <Text style={[styles.itemName, item.purchased && styles.strikeThrough]}>
                {item.name}
              </Text>
              <Text style={styles.itemDetails}>{item.quantity} {item.unit}</Text>
            </TouchableOpacity>

            <TouchableOpacity 
              onPress={() => deleteItem(item.id)}
              style={styles.deleteButton}
            >
              <Text style={{ color: 'red', fontWeight: 'bold' }}>X</Text>
            </TouchableOpacity>
          </View>
        )}
        ListEmptyComponent={<Text style={styles.empty}>Ta seznam še nima artiklov.</Text>}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#f5f5f5' },
  inputContainer: { flexDirection: 'row', marginBottom: 20, gap: 10, alignItems: 'center' },
  input: { backgroundColor: '#fff', padding: 10, borderRadius: 5, borderWidth: 1, borderColor: '#ddd' },
  itemRow: { 
    padding: 15, 
    backgroundColor: 'white', 
    marginBottom: 10, 
    borderRadius: 8, 
    flexDirection: 'row', 
    justifyContent: 'space-between',
    alignItems: 'center',
    elevation: 2 
  },
  itemName: { fontSize: 16, fontWeight: 'bold' },
  itemDetails: { color: '#666' },
  purchasedRow: { backgroundColor: '#f0f0f0', opacity: 0.6 },
  strikeThrough: { textDecorationLine: 'line-through', color: 'gray' },
  deleteButton: { padding: 10, marginLeft: 10 },
  empty: { textAlign: 'center', marginTop: 50, color: 'gray' }
});

export default ItemsScreen;