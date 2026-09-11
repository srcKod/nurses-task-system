<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class PatientResource extends JsonResource
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
            'room_photo_path'=>$this->room_photo_path,
            'is_stopped'=>$this->is_stopped,
            'created_at'=>$this->created_at->format('Y/m/d h:i:s A')
        ];
    }
}
