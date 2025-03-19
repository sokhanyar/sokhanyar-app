package ir.saltech.sokhanyar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.saltech.sokhanyar.BaseApplication.FriendshipStatus
import ir.saltech.sokhanyar.data.local.entity.Device
import ir.saltech.sokhanyar.data.local.entity.Media
import ir.saltech.sokhanyar.data.local.entity.User
import ir.saltech.sokhanyar.data.local.entity.UserAvatar
import ir.saltech.sokhanyar.data.local.entity.UserFriend

@Dao
interface UserDao {
	@Query("select * from user")
	fun getAll(): List<User>

	@Query("select device.* from user join device on user.id == device.userId")
	fun getAllDevice(): List<Device>

	@Query("select * from user where displayName like :displayName limit 1")
	fun findByDisplayName(displayName: String): User

	@Query("select * from user where phoneNumber=:phoneNumber")
	fun findByPhoneNumber(phoneNumber: String): User

	@Query("select media.* from user_avatar as avatar join media on media.id == avatar.mediaId group by avatar.userId having avatar.userId == :mediaId and media.id == :mediaId")
	fun getAvatarMediaByMediaId(mediaId: String): Media

	@Query("select avatar.* from user join user_avatar as avatar on user.id == avatar.userId group by mediaId having avatar.userId == :userId and user.id == :userId")
	fun getUserAvatarsByUserId(userId: String): List<UserAvatar>

	@Query("SELECT * FROM user_friend WHERE userId = :userId AND status = :status")
	fun getFriendsByStatus(userId: String, status: FriendshipStatus): List<UserFriend>

	@Query("SELECT * FROM user WHERE id IN (SELECT friendId FROM user_friend WHERE userId = :userId AND status = 'Accepted')")
	fun getAcceptedFriends(userId: String): List<User>

	@Insert
	fun add(user: User)

	@Update
	fun update(user: User)

	@Delete
	fun remove(user: User)
}
