import { useState, useEffect } from 'react';
import { 
  StyleSheet, Text, View, Button, ActivityIndicator, 
  FlatList, StatusBar, TextInput, Alert, TouchableOpacity 
} from 'react-native';
import { useAuth0 } from 'react-native-auth0';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

const HomeScreen = ({ navigation }) => {
  const insets = useSafeAreaInsets();
  const { authorize, clearSession, user, isLoading, getCredentials } = useAuth0();
  
  const [accessToken, setAccessToken] = useState(null);
  const [lists, setLists] = useState([]);
  const [isFetching, setIsFetching] = useState(false);
  const [newListName, setNewListName] = useState('');

  const onLogin = async () => {
    try {
      await authorize({ audience: 'https://shopsync-api.com', scope: 'openid profile email read:items' }); 
      const credentials = await getCredentials();
      setAccessToken(credentials.accessToken);
    } catch (e) { console.log(e); }
  };

  const fetchLists = async () => {
    if (!accessToken) return;
    setIsFetching(true);
    try {
      const response = await fetch('http://10.0.2.2:8080/api/shopping-lists', {
        headers: { 'Authorization': `Bearer ${accessToken}` }
      });
      if (response.ok) {
        const data = await response.json();
        setLists(data);
      }
    } catch (err) { console.log(err); }
    finally { setIsFetching(false); }
  };

  const handleCreateList = async () => {
    if (!newListName.trim()) return;
    try {
      const response = await fetch('http://10.0.2.2:8080/api/shopping-lists', {
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
    } catch (err) { Alert.alert("Napaka", "Ni bilo mogoče ustvariti seznama."); }
  };

  useEffect(() => {
    if (accessToken) fetchLists();
  }, [accessToken]);

  if (isLoading) return <View style={styles.center}><ActivityIndicator size="large" /></View>;

  return (
    <View style={[styles.container, { paddingTop: insets.top, paddingBottom: insets.bottom }]}>
      <StatusBar barStyle="dark-content" />
      {accessToken ? (
        <View style={styles.content}>
          <Text style={styles.welcome}>Zdravo, {user?.name}!</Text>
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
                <Text style={{color: '#666'}}>Dotakni se za artikle →</Text>
              </TouchableOpacity>
            )}
            refreshing={isFetching}
            onRefresh={fetchLists}
            ListEmptyComponent={<Text style={styles.empty}>Nimaš še nobenega seznama.</Text>}
          />
          <Button title="Odjava" onPress={() => { clearSession(); setAccessToken(null); }} color="red" />
        </View>
      ) : (
        <View style={styles.center}>
          <Button title="Vstopi v ShopSync" onPress={onLogin} />
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  content: { flex: 1, paddingHorizontal: 20 },
  welcome: { fontSize: 18, marginBottom: 20, textAlign: 'center' },
  inputContainer: { flexDirection: 'row', marginBottom: 20 },
  input: { flex: 1, backgroundColor: '#fff', padding: 10, borderRadius: 5, marginRight: 10, borderWidth: 1, borderColor: '#ddd' },
  listItem: { backgroundColor: '#fff', padding: 15, borderRadius: 10, marginBottom: 10, elevation: 2, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  itemText: { fontSize: 18, fontWeight: '500' },
  empty: { textAlign: 'center', color: '#999', marginTop: 40 }
});

export default HomeScreen;