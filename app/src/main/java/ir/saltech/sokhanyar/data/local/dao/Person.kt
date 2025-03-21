package ir.saltech.sokhanyar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.saltech.sokhanyar.BaseApplication.FriendshipStatus
import ir.saltech.sokhanyar.BaseApplication.UserRole
import ir.saltech.sokhanyar.data.local.entity.Device
import ir.saltech.sokhanyar.data.local.entity.Media
import ir.saltech.sokhanyar.data.local.entity.User
import ir.saltech.sokhanyar.data.local.entity.UserAvatar
import ir.saltech.sokhanyar.data.local.entity.UserFriend

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE role = :role") 
    fun getByRole(role: UserRole): List<User>

    @Query("SELECT * FROM user WHERE phoneNumber = :phoneNumber")
    fun findByPhoneNumber(phoneNumber: String): User?
    
    @Query("SELECT * FROM user WHERE clinicId = :clinicId")
    fun getByClinicId(clinicId: String): List<User>

    @Query("SELECT device.* FROM user JOIN device ON user.id == device.userId")
    fun getAllDevice(): List<Device>

    @Query("SELECT * FROM user WHERE displayName LIKE :displayName LIMIT 1")
    fun findByDisplayName(displayName: String): User

    @Query("SELECT media.* FROM user_avatars AS avatar JOIN media ON media.id == avatar.mediaId GROUP BY avatar.userId HAVING avatar.userId == :mediaId AND media.id == :mediaId")
    fun getAvatarMediaByMediaId(mediaId: String): Media

    @Query("SELECT avatar.* FROM user JOIN user_avatars AS avatar ON user.id == avatar.userId GROUP BY mediaId HAVING avatar.userId == :userId AND user.id == :userId")
    fun getUserAvatarsByUserId(userId: String): List<UserAvatar>

    @Query("SELECT * FROM user_friends WHERE userId = :userId AND status = :status")
    fun getFriendsByStatus(userId: String, status: FriendshipStatus): List<UserFriend>

    @Query("SELECT * FROM user WHERE id IN (SELECT friendId FROM user_friends WHERE userId = :userId AND status = 'Accepted')")
    fun getAcceptedFriends(userId: String): List<User>

    @Insert
    fun add(user: User)

    @Delete 
    fun remove(user: User)

    @Update
    fun update(user: User)
}
