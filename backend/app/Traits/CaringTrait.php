<?php

namespace App\Traits;

use Illuminate\Http\Request;

trait CaringTrait
{
    public function saveRequestData(Request $request, $caring): void
    {
        $caring->nurse_id = ($request->nurse_id != null)?$request->nurse_id:$caring->nurse_id;
        $caring->caringtype_id = ($request->caringtype_id != null)?$request->caringtype_id:$caring->caringtype_id;
        $caring->patient_id = ($request->patient_id != null)?$request->patient_id:$caring->patient_id;
        $caring->time = ($request->time != null)?$request->time:$caring->time;
        $caring->description = $request->description;
        $caring->is_finished = ($request->is_finished != null)?$request->is_finished:$caring->is_finished;
        $caring->save();
    }
}
