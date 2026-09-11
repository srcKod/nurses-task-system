<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

/**
 * Class Caring.
 *
 * @author  developer <developer@example.com>
 *
 * @OA\Schema(
 *   description="Caring",
 *   title="Caring",
 *   required={"time"},
 *   @OA\Property(type="integer",description="Caring id",title="id",property="id",example="1"),
 *   @OA\Property(type="timestamp",description="Caring time",title="time",property="time",example="2023-04-16 05:04:01 PM"),
 *   @OA\Property(type="integer",description="Caring-Nurse",title="nurse_id",property="nurse_id",example="1"),
 *   @OA\Property(type="integer",description="Caring-CaringType",title="caringtype_id",property="caringtype_id",example="3"),
 *   @OA\Property(type="integer",description="Caring-Patient",title="patient_id",property="patient_id",example="10"),
 *   @OA\Property(type="string",description="Caring Description",title="description",property="description",example="Give Medicine"),
 *   @OA\Property(type="boolean",description="Caring Status",title="is_finished",property="is_finished",example="0"),
 * )
 * @OA\Schema(
 *   schema="Carings",
 *   title="Carings",
 *   @OA\Property(title="Carings",property="Nurse",type="array",
 *     @OA\Items(type="object",ref="#/components/schemas/Caring"),
 *   )
 * )
 * @OA\Parameter(
 *   parameter="Caring--id",
 *   in="path",
 *   name="id",
 *   required=true,
 *   description="Caring Id",
 *   @OA\Schema(
 *     type="integer",
 *     format="int64",
 *     example="1",
 *   )
 * ),
 **/
class Caring extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'time','description','is_finished'
    ];

    protected $casts = [
        'time' => 'datetime',
    ];

    public function nurse(){
        return $this->belongsTo(Nurse::class);
    }
    public function caringtype(){
        return $this->belongsTo(Caringtype::class);
    }
    public function patient(){
        return $this->belongsTo(Patient::class);
    }
}
