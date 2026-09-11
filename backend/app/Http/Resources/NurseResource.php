<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class NurseResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     *
     * @return array<string, mixed>
     */
    public function toArray(Request $request): array
    {
//        return parent::toArray($request);
        return [
            'id'=>$this->id,
            'name'=>$this->name,
            'email'=>$this->email,
            'gender'=>$this->gender,
            'phone'=>$this->phone,
            'is_resigned'=>$this->is_resigned,
            'is_admin'=>$this->is_admin,
            'created_at'=>$this->created_at->format('Y-m-d h:i:s A'),
            'updated_at'=>$this->created_at->format('Y-m-d h:i:s A')
        ];
    }
}
