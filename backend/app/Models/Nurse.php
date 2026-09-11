<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;
use Tymon\JWTAuth\Contracts\JWTSubject;

/**
 * Class Nurse.
 *
 * @author  developer <developer@example.com>
 *
 * @OA\Schema(
 *   description="Nurse",
 *   title="Nurse",
 *   required={"name","email","password"},
 *   @OA\Property(type="integer",description="Nurse id",title="id",property="id",example="1"),
 *   @OA\Property(type="string",description="Nurse Name (Max:50)",title="name",property="name",example="ahmad"),
 *   @OA\Property(type="string",description="Nurse Email (Should be Unique)",title="email",property="email",example="ahmad@example.com"),
 *   @OA\Property(type="string",description="Nurse password (Min:8)",title="password",property="password",example="12345678"),
 *   @OA\Property(type="boolean",description="Nurse gender",title="gender",property="gender",example="1"),
 *   @OA\Property(type="string",description="Nurse phone",title="phone",property="phone",example="0999111222"),
 *   @OA\Property(type="boolean",description="Resigning status",title="is_resigned",property="is_resigned",example="0"),
 *   @OA\Property(type="boolean",description="Nurse Role (admin|user)",title="is_admin",property="is_admin",example="0"),
 *   @OA\Property(type="string",description="Create Date)",title="Create Date",property="created_at",example="2023-04-19 12:37:32 PM"),
 *   @OA\Property(type="string",description="Update Date",title="Update Date",property="updated_at",example="2023-04-19 10:37:32 AM"),
 * )
 * @OA\Schema(
 *   schema="Nurses",
 *   title="Nurses",
 *   @OA\Property(title="Nurses",property="Nurse",type="array",
 *     @OA\Items(type="object",ref="#/components/schemas/Nurse"),
 *   )
 * )
 * @OA\Parameter(
 *   parameter="Nurse--id",
 *   in="path",
 *   name="id",
 *   required=true,
 *   description="Nurse Id",
 *   @OA\Schema(
 *     type="integer",
 *     format="int64",
 *     example="1",
 *   )
 * ),
 **/
class Nurse extends Authenticatable implements JWTSubject
{
    use HasApiTokens, HasFactory, Notifiable;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'name',
        'gender',
        'phone',
        'is_resigned',
        'is_admin',
        'email',
        'password',
        'fcm_token'
    ];

    /**
     * The attributes that should be hidden for serialization.
     *
     * @var array<int, string>
     */
    protected $hidden = [
        'password',
        'remember_token'
    ];

    /**
     * The attributes that should be cast.
     *
     * @var array<string, string>
     */
    protected $casts = [
        'email_verified_at' => 'datetime',
    ];

    public function carings(){
        return $this->hasMany(Caring::class);
    }

    /**
     * Specifies the user's FCM tokens
     *
     * @return string|array
     */
    public function routeNotificationForFcm(): array|string
    {
            return $this->fcm_token;
            //return $this->getDeviceTokens();
    }

    public function getJWTIdentifier()
    {
        return $this->getKey();
    }

    public function getJWTCustomClaims()
    {
        return [];
    }
}
