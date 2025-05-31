package com.example.CodeHub.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_views", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "view_date"})
})
public class UserView {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "view_date", nullable = false)
    private LocalDate viewDate;
    
    public UserView() {
    }
    
    public UserView(User user, LocalDate viewDate) {
        this.user = user;
        this.viewDate = viewDate;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDate getViewDate() {
        return viewDate;
    }
    
    public void setViewDate(LocalDate viewDate) {
        this.viewDate = viewDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserView userView = (UserView) o;
        
        if (!user.getId().equals(userView.user.getId())) return false;
        return viewDate.equals(userView.viewDate);
    }
    
    @Override
    public int hashCode() {
        int result = user.getId().hashCode();
        result = 31 * result + viewDate.hashCode();
        return result;
    }
}
