<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

/**
 * Class Caringtype.
 *
 * @author  developer <developer@example.com>
 *
 * @OA\Schema(
 *   description="Caringtype",
 *   title="Caringtype",
 *   required={"name"},
 *   @OA\Property(type="integer",description="Caringtype id",title="Caringtype id",property="id",example="1"),
 *   @OA\Property(type="string",description="Caringtype Name",title="name",property="name",example="Medication"),
 *   @OA\Property(type="string",description="Caringtype Description",title="description",property="description",example="Give patient medication"),
 * )
 * @OA\Schema(
 *   schema="Caringtypes",
 *   title="Caringtypes",
 *   @OA\Property(title="Caringtypes",property="Caringtype",type="array",
 *     @OA\Items(type="object",ref="#/components/schemas/Caringtype"),
 *   )
 * )
 * @OA\Parameter(
 *   parameter="Caringtype--id",
 *   in="path",
 *   name="id",
 *   required=true,
 *   description="Caringtype Id",
 *   @OA\Schema(
 *     type="integer",
 *     format="int64",
 *     example="1",
 *   )
 * ),
 **/
class Caringtype extends Model
{
    use HasFactory;

    protected $fillable=[
        'name','description'
    ];
    // define the relationship
    public function carings(){
        return $this->hasMany(Caring::class);
    }
}
