<x-app-layout>
    <x-slot name="header">
        <div class="flex justify-between">
            <h2 class="font-semibold text-xl text-gray-800 leading-tight">
                {{ __('Carings') }}
            </h2>
        </div>
    </x-slot>
    <x-splade-modal position="top" max-width="2xl">
        <div class="py-10">
            <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
                <div class="bg-white shadow-sm sm:rounded-lg">
                    <div class="p-1 text-gray-900">
                        <h1 class="text-center font-bold">Update Caring Type</h1>

{{--                        @dd($caring->id)--}}
                        @if(!$caring->deleted_at)
                            @php($route = route('carings.update',$caring))
                        @else
                            @php($route = route('carings.update-trashed',$caring->id))
                        @endif

                        <x-splade-form confirm :default="$caring" method="PATCH" :action="$route" class="max-w-md mx-auto p-4 bg-white rounded-md">
                            @csrf
                            <x-splade-input name="time" label="Time" required date time="{ time_24hr: false }"/>
{{--                            <x-splade-select name="nurse" label="Nurse">--}}
{{--                                <option value={{$caring->nurse_id}}>{{$caring->nurse->name}}</option>--}}
{{--                            </x-splade-select>--}}
{{--                            <x-splade-select name="caringtype" label="Caring Type">--}}
{{--                                <option value={{$caring->caringtype_id}}>{{$caring->caringtype->name}}</option>--}}
{{--                            </x-splade-select>--}}
{{--                            <x-splade-select name="patient" label="Patient">--}}
{{--                                <option value={{$caring->patient_id}}>{{$caring->patient->name}}</option>--}}
{{--                            </x-splade-select>--}}
                            <x-splade-select name="nurse_id" label="Nurse" :options="$nurses"
                                             option-label="name" option-value="id"  required/>
                            <x-splade-select name="caringtype_id" label="Caring Type" :options="$caringtypes"
                                             option-label="name" option-value="id"  required/>
                            <x-splade-select name="patient_id" label="Patient" :options="$patients"
                                             option-label="name" option-value="id"  required/>
{{--                            {{$caring->is_finished}}--}}
                            <x-splade-checkbox class="mt-4" name="is_finished" value="$caring->is_finishe" label="Status" />
                            <x-splade-textarea class="mt-4" name="description" label="Description" max="300" autosize />
                            <x-splade-submit class="mt-4" label="Update"/>
                        </x-splade-form>
                    </div>
                </div>
            </div>
        </div>
    </x-splade-modal>
</x-app-layout>

