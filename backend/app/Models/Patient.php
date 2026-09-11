<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

/**
 * Class Patient.
 *
 * @author  developer <developer@example.com>
 *
 * @OA\Schema(
 *   description="Patient",
 *   title="Patient",
 *   required={"name"},
 *   @OA\Property(type="integer",description="Patient id",title="id",property="id",example="1"),
 *   @OA\Property(type="string",description="Patient Name",title="name",property="name",example="ahmad"),
 *   @OA\Property(type="string",description="Patient Room Photo URL",title="room_photo_path",property="room_photo_path",example="https://example.com/img.png"),
 *   @OA\Property(type="boolean",description="Patient Status",title="Status",property="is_stopped",example="0"),
 * )
 * @OA\Schema(
 *   schema="Patients",
 *   title="Patients",
 *   @OA\Property(title="Patients",property="Patient",type="array",
 *     @OA\Items(type="object",ref="#/components/schemas/Patient"),
 *   )
 * )
 * @OA\Parameter(
 *   parameter="Patient--id",
 *   in="path",
 *   name="id",
 *   required=true,
 *   description="Patient Id",
 *   @OA\Schema(
 *     type="integer",
 *     format="int64",
 *     example="1",
 *   )
 * ),
 **/

class Patient extends Model
{
    use HasFactory;

    protected $fillable=[
        'name','room_photo_path','is_stopped'
    ];
    // define the relationship
    public function carings(){
        return $this->hasMany(Caring::class);
    }
}
