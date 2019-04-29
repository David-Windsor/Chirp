package davidwindsor.firstproj.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * User class
 * Goal of this class is to have an easy container that is able to supply relevant user info
 * and be able to tell the server when there is an update to itself
 */

public class User implements Serializable {
    private String handle;
    private String email;



    private String password;
    private String id;
    private ArrayList<Chirp> chirps;

    public User() {
        //make a default user for tests
        email = "";
        password = "";
        handle = "";
        id = "";
        chirps = new ArrayList<>();
    }

    public User(String email, String handle, String password, String ID) {
        setEmail(email);
        setHandle(handle);
        setPassword(password);
        setId(ID);
        chirps = new ArrayList<>();
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Chirp> getChirps() {
        return chirps;
    }

    public void setChirps(ArrayList<Chirp> chirps) {
        this.chirps = chirps;
    }


}
