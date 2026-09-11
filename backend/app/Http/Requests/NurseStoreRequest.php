<?php

namespace App\Http\Requests;

use App\Models\Nurse;
use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Http\Exceptions\HttpResponseException;
use Illuminate\Validation\Rule;

class NurseStoreRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array<string, mixed>
     */
    public function rules()
    {
        return [
            'name'=> 'required|max:50',
            'email'=>'required|email|unique:nurses',
            'password'=> 'required|min:8'
        ];
    }

    public function failedValidation(Validator $validator)
    {
        throw new HttpResponseException(response()->json([
            'success'   => false,
            'message'   => 'Validation errors',
            'data'      => $validator->errors()
        ]));
    }

    // Customizing The Error Messages

    /**
     * Get the error messages for the defined validation rules.
     *
     * @return array<string, string>
     */
    public function messages(): array
    {
        return [
            'name.required' => 'A name is required',
            'name.max' => 'A name should be 50 characters only',
            'phone.max' => 'A phone should be less than 20 number',
            'email.required' => 'An email is required',
            'email.unique' => 'The email has already been taken.',
            'email.email' => 'Input should be a valid email address',
            'password.required' => 'A password is required',
        ];
    }
}
