<?php

namespace App\Http\Resources;

use App\Models\Caringtype;
use App\Models\Nurse;
use App\Models\Patient;
use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class CaringResource extends JsonResource
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
            'nurse_id'=>$this->nurse_id,
            'nurse_name'=>Nurse::with('carings')->find($this->nurse_id)->name,
            'caringtype_id'=>$this->caringtype_id,
            'caringtype_name'=>Caringtype::with('carings')->find($this->caringtype_id)->name,
            'patient_id'=>$this->patient_id,
            'patient_name'=>Patient::with('carings')->find($this->patient_id)->name,
            'time'=>$this->time->format('Y-m-d h:i:s A'),
            'description'=>$this->description,
            'status'=>($this->is_finished)?"Finished":"Pending",
            'created_at'=>$this->created_at->format('Y-m-d g:i:s A'),
            'deleted_at'=>($this->deleted_at != null)? $this->deleted_at->format('Y-m-d g:i:s A'):null
        ];
    }
}
